package minestack;

import com.google.inject.AbstractModule;
import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import de.exlll.configlib.YamlConfigurations;
import io.papermc.paper.plugin.bootstrap.PluginProviderContext;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Collection;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("UnstableApiUsage")
@Slf4j(topic = "Minestack")
@RequiredArgsConstructor
public final class MinestackModule extends AbstractModule {
    private final PluginProviderContext context;

    @Override
    protected void configure() {
        var config = configuration();
        var sslContext = sslContext(config);
        database(config.mongo, mongoClient(config.mongo, sslContext));
        hazelcast(config.hazelcast, sslContext);
    }

    private void database(MinestackConfig.MongoConfig cfg, @NonNull MongoClient client) {
        var db = client.getDatabase(cfg.database);
        var _ = db.listCollectionNames().first();
        bind(MongoDatabase.class).toInstance(db);
    }

    private void hazelcast(MinestackConfig.HazelcastConfig cfg, @NonNull SSLContext sslContext) {
        var start = System.currentTimeMillis();
        try {
            var config = new Config();
            config.setClusterName(cfg.clusterName);

            var net = config.getNetworkConfig();
            net.setPortAutoIncrement(true);

            var join = net.getJoin();
            join.getMulticastConfig().setEnabled(false);

            if (cfg.hostname != null) {
                var tcp = join.getTcpIpConfig();
                tcp.setEnabled(true);
                tcp.addMember(cfg.hostname);
            }

            config.setProperty("hazelcast.logging.type", "slf4j");
            config.setProperty("hazelcast.phone.home.enabled", "false");
            config.setProperty("hazelcast.discovery.enabled", "true");

            if (cfg.sslEnabled) {
                var ssl = net.getSSLConfig();
                ssl.setEnabled(true);
                ssl.setFactoryImplementation(sslContext);
                log.info("[Hazelcast] TLS/mTLS enabled for Hazelcast client connections");
            }

            var client = Hazelcast.newHazelcastInstance(config);
            bind(HazelcastInstance.class).toInstance(client);
            log.info("[Hazelcast] initialized in {}ms", System.currentTimeMillis() - start);
        } catch (Exception exception) {
            log.error("[Hazelcast] Cannot initialize Hazelcast client", exception);
            System.exit(1);
        }
    }

    private MongoClient mongoClient(@NonNull MinestackConfig.MongoConfig cfg, @NonNull SSLContext sslContext) {
        var start = System.currentTimeMillis();
        try {
            var connectionString = new ConnectionString(cfg.url);
            var builder = MongoClientSettings.builder().applyConnectionString(connectionString);

            if (cfg.sslEnabled) {
                builder.applyToSslSettings(ss -> ss.enabled(true).context(sslContext));
                log.info("[MongoDB] TLS/mTLS enabled for MongoDB client connections");
            }

            builder.applyToConnectionPoolSettings(pool -> pool.maxSize(cfg.maxPoolSize));

            builder.applyToSocketSettings(s -> s.connectTimeout(10, TimeUnit.SECONDS));
            builder.applyToClusterSettings(c -> c.serverSelectionTimeout(10, TimeUnit.SECONDS));

            var client = MongoClients.create(builder.build());
            client.getDatabase("admin").runCommand(new Document("ping", 1));
            bind(MongoClient.class).toInstance(client);
            log.info("[MongoDB] Initialized in {}ms", System.currentTimeMillis() - start);
            return client;
        } catch (Exception exception) {
            log.error("[MongoDB] Cannot initialize MongoDB client", exception);
            System.exit(1);
            return null;
        }
    }

    private MinestackConfig configuration() {
        var df = context.getDataDirectory().toFile();
        if (!df.exists()) {
            if (!df.mkdirs())
                throw new IllegalStateException("Cannot create directory `" + df.getPath() + "`");
        }
        var file = new File(df, "config.yml");
        var path = file.toPath();
        if (!file.exists()) {
            YamlConfigurations.save(path, MinestackConfig.class, new MinestackConfig());
        }

        var config = YamlConfigurations.load(path, MinestackConfig.class);
        bind(MinestackConfig.class).toInstance(config);
        return config;
    }

    private SSLContext sslContext(@NonNull MinestackConfig cfg) {
        var start = System.currentTimeMillis();
        try {
            var env = System.getenv("MINESTACK_CERT_DIRECTORIES");
            if (env != null && !env.isEmpty()) {
                for (var path : env.split(",")) {
                    if (cfg.certDirs.contains(path)) continue;
                    cfg.certDirs.add(path);
                }
            }

            var property = System.getProperty("minestack.cert.directories", null);
            if (property != null && !property.isEmpty()) {
                for (var path : property.split(",")) {
                    if (cfg.certDirs.contains(path)) continue;
                    cfg.certDirs.add(path);
                }
            }

            var context = SSLContext.getInstance("TLS");

            Collection<File> caFiles = new ObjectArrayList<>();
            Collection<File> p12Files = new ObjectArrayList<>();
            Collection<File> pemCertFiles = new ObjectArrayList<>();
            Collection<File> pemKeyFiles = new ObjectArrayList<>();

            for (var directoryPath : cfg.certDirs) {
                var directory = new File(directoryPath);
                if (!directory.exists() || !directory.isDirectory()) {
                    throw new IllegalArgumentException("Configured SSL directory does not exist: " + directory.getPath());
                }

                for (var file : Objects.requireNonNull(directory.listFiles())) {
                    String name = file.getName().toLowerCase(Locale.ROOT);

                    if (name.endsWith(".ca.crt") || name.endsWith(".ca.pem")) {
                        caFiles.add(file);
                    } else if (name.endsWith(".p12")) {
                        p12Files.add(file);
                    } else if (name.endsWith(".crt")) {
                        pemCertFiles.add(file);
                    } else if (name.endsWith(".key")) {
                        pemKeyFiles.add(file);
                    } else {
                        log.warn("[SSL] Cannot handle SSL file `{}`. Unknown extension", file.getPath());
                    }
                }
            }

            var ts = KeyStore.getInstance(KeyStore.getDefaultType());
            ts.load(null, null);
            var cf = CertificateFactory.getInstance("X.509");

            for (var caFile : caFiles) {
                try (var fis = new FileInputStream(caFile)) {
                    var cert = (X509Certificate) cf.generateCertificate(fis);
                    var alias = caFile.getName();
                    ts.setCertificateEntry(alias, cert);
                    log.info("[SSL] Loaded CA Certificate: {}", alias);
                }
            }

            var tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(ts);

            var ks = KeyStore.getInstance(KeyStore.getDefaultType());
            ks.load(null, null);

            for (var p12File : p12Files) {
                try (var fis = new FileInputStream(p12File)) {
                    ks.load(fis, new char[0]);
                    log.info("[SSL] Loaded PKCS12 KeyStore: {}", p12File.getName());
                }
            }

            for (var crtFile : pemCertFiles) {
                String keyFileName = crtFile.getName().replaceAll("\\.crt$", ".key");
                var keyFile = pemKeyFiles.stream().filter(k -> k.getName().equals(keyFileName)).findFirst().orElse(null);
                if (keyFile == null) {
                    log.warn("[SSL] Cert file might be missing a key file: {}", crtFile.getPath());
                    continue;
                }

                try (var fis = new FileInputStream(crtFile)) {
                    var cert = (X509Certificate) cf.generateCertificate(fis);

                    var keyPem = new String(Files.readAllBytes(keyFile.toPath()))
                            .replace("-----BEGIN PRIVATE KEY-----", "")
                            .replace("-----END PRIVATE KEY-----", "")
                            .replaceAll("\\s+", "");

                    var keyBytes = Base64.getDecoder().decode(keyPem);
                    var spec = new PKCS8EncodedKeySpec(keyBytes);
                    var privateKey = KeyFactory.getInstance("RSA").generatePrivate(spec);

                    ks.setKeyEntry(crtFile.getName(), privateKey, new char[0], new Certificate[]{cert});
                    log.info("[SSL] Loaded PEM key pair: {}", crtFile.getName());
                }
            }

            var kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(ks, new char[0]);

            context.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

            log.info("[SSL] SSLContext initialized successfully from configured directories in {}ms", System.currentTimeMillis() - start);
            return context;
        } catch (Exception exception) {
            log.error("[SSL] Cannot load configured SSLContext", exception);
            System.exit(1);
            return null;
        }
    }
}
