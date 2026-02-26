package minestack;

import com.google.inject.Singleton;
import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;

import java.util.List;

@Configuration
@Singleton
public class MinestackConfig {
    @Comment({"List of directories containing client certificates, keys and certificate authorities"})
    public List<String> certDirs = List.of();

    public HazelcastConfig hazelcast = new HazelcastConfig();

    public MongoConfig mongo = new MongoConfig();

    @Configuration
    public static class HazelcastConfig {
        @Comment({"Cluster name"})
        public String clusterName = "minestack";

        @Comment({"Cluster hostname (for DNS service discovery)"})
        public String hostname = null;

        @Comment({"Enable TLS/SSL"})
        public boolean sslEnabled = false;
    }

    @Configuration
    public static class MongoConfig {
        @Comment({"MongoDB .",
                "Example: mongodb://admin:adminpass@localhost:27017/"})
        public String url = "mongodb://admin:adminpass@localhost:27017/";

        @Comment({"Database name"})
        public String database = "minestack";

        @Comment({"Enable TLS/SSL"})
        public boolean sslEnabled = false;

        @Comment({"Maximum connection pool size"})
        public int maxPoolSize = 10;
    }
}
