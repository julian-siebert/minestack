package minestack.plugin;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.google.inject.Stage;
import com.hazelcast.core.HazelcastInstance;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import de.exlll.configlib.YamlConfigurations;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import minestack.MinecraftEdition;
import minestack.Minestack;
import minestack.actions.Action;
import minestack.mongo.MongoFactoryModule;
import minestack.option.Option;
import minestack.permission.PermissionRegistry;
import minestack.permission.PermissionRegistryImpl;
import minestack.plugin.chat.SystemChatListener;
import minestack.plugin.listeners.PlayerJoinListener;
import minestack.plugin.listeners.PlayerQuitListener;
import minestack.plugin.roles.RolesCommand;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import minestack.registry.Registry;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.geysermc.api.Geyser;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Slf4j (topic = "MineStack")
@Singleton
public final class MinestackPlugin extends JavaPlugin implements Minestack {
    private static final Map<Plugin, Collection<Class<? extends Listener>>> LISTENERS = new Object2ObjectArrayMap<>();

    private final Collection<AbstractModule> modules = new ObjectArrayList<>();
    private final CompletableFuture<Injector> injector = new CompletableFuture<>();

    private final CompletableFuture<PermissionRegistry> permissionRegistry = new CompletableFuture<>();

    private final HazelcastInstance hz;
    private final MongoClient mongo;
    private final MongoDatabase db;

    private boolean geyser = false;
    private boolean floodgate = false;

    @Inject
    public MinestackPlugin(HazelcastInstance hz, MongoClient mongo, MongoDatabase db) {
        this.hz = hz;
        this.mongo = mongo;
        this.db = db;
    }

    @Override
    public void onLoad() {
        modules.add(new MinestackPluginModule(this));
        modules.add(new MongoFactoryModule(db));

        var em = PacketEvents.getAPI().getEventManager();
        em.registerListener(new SystemChatListener(), PacketListenerPriority.HIGHEST);

        injector.complete(Guice.createInjector(Stage.PRODUCTION, modules));
    }

    @Override
    public void onEnable() {
        var pm = Bukkit.getPluginManager();
        this.geyser = pm.isPluginEnabled("Geyser-Spigot");
        this.floodgate = pm.isPluginEnabled("floodgate");

        var lm = getLifecycleManager();
        lm.registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            var registrar = commands.registrar();
            registrar.register(RolesCommand.createCommand(), "Manage roles");
        });

        register(this, PlayerJoinListener.class);
        register(this, PlayerQuitListener.class);
        permissionRegistry.complete(registry(PermissionRegistry.class));
    }

    @Override
    public void onDisable() {
        Bukkit.shutdown();
        HandlerList.unregisterAll();
        hz.shutdown();
        mongo.close();
    }

    @Override
    public Option<MinecraftEdition> edition(@NonNull OfflinePlayer player) {
        if (!this.geyser) return Option.none();
        if (Geyser.api().isBedrockPlayer(player.getUniqueId()))
            return Option.some(MinecraftEdition.BEDROCK);
        return Option.some(MinecraftEdition.JAVA);
    }

    @Override
    public <T extends Record & Action> void trigger(@NotNull Plugin plugin, T action) {

    }

    @Override
    public <T extends Record & Action> CompletableFuture<Void> triggerAsync(@NotNull Plugin plugin, T action) {
        return CompletableFuture.runAsync(() -> trigger(plugin, action));
    }

    @Override
    public <T extends Listener> T register(@NonNull Plugin plugin, @NonNull Class<T> clazz) {
        synchronized (LISTENERS) {
            var listeners = LISTENERS.computeIfAbsent(plugin, _ -> new ObjectArrayList<>());
            listeners.add(clazz);
            var injector = this.injector.join();
            var listener = injector.getInstance(clazz);
            Bukkit.getPluginManager().registerEvents(listener, plugin);
            return listener;
        }
    }

    @Override
    public <T extends Registry> T registry(Class<T> registryClass) {
        var injector = this.injector.join();
        return injector.getInstance(registryClass);
    }

    @Override
    public PermissionRegistry permissionRegistry() {
        return permissionRegistry.join();
    }

    @Override
    public <T> Optional<T> config(@NonNull Plugin plugin, @NonNull String name, @NonNull Class<T> tClass) {
        var df = plugin.getDataFolder();
        if (!df.exists()) {
            var _ = df.mkdirs();
        }
        var file = new File(df, name);
        var path = file.toPath();
        if (!file.exists()) {
            return Optional.empty();
        }
        return Optional.of(YamlConfigurations.load(path, tClass));
    }

    @Override
    public @NotNull <T> T config(@NonNull Plugin plugin, @NonNull String name, @NonNull Class<T> tClass, @NonNull T defaultConfig) {
        var df = plugin.getDataFolder();
        if (!df.exists()) {
            var _ = df.mkdirs();
        }
        var file = new File(df, name);
        var path = file.toPath();
        if (!file.exists()) {
            YamlConfigurations.save(path, tClass, defaultConfig);
        }
        return YamlConfigurations.load(path, tClass);
    }

    @Override
    public Injector injector() {
        return injector.join();
    }
}
