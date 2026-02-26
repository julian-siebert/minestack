package minestack.plugin;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.bootstrap.PluginProviderContext;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.extern.slf4j.Slf4j;
import minestack.MinestackModule;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

@SuppressWarnings("UnstableApiUsage")
@Slf4j (topic = "minestack")
public final class MinestackPluginBootstrapper implements PluginBootstrap {
    private final CompletableFuture<Injector> injector = new CompletableFuture<>();
    private final Collection<AbstractModule> modules = new ObjectArrayList<>();

    @Override
    public void bootstrap(@NonNull BootstrapContext context) {
        try {
            modules.add(new MinestackModule(context));
            injector.complete(Guice.createInjector(Stage.PRODUCTION, modules));
        } catch (Exception exception) {
            log.error("Cannot bootstrap Minestack", exception);
            System.exit(1);
        }
    }

    @Override
    public @NonNull JavaPlugin createPlugin(@NonNull PluginProviderContext context) {
        MinestackPlugin plugin = injector.join().getInstance(MinestackPlugin.class);
        Internal.init(plugin);
        return plugin;
    }
}
