package minestack.plugin;

import com.google.inject.AbstractModule;
import lombok.RequiredArgsConstructor;
import minestack.Minestack;
import minestack.permission.PermissionRegistry;
import minestack.permission.PermissionRegistryImpl;

@RequiredArgsConstructor
public final class MinestackPluginModule extends AbstractModule {
    private final MinestackPlugin plugin;

    @Override
    protected void configure() {
        bind(Minestack.class).toInstance(plugin);
        bind(MinestackPlugin.class).toInstance(plugin);
        bind(PermissionRegistry.class).toInstance(new PermissionRegistryImpl(plugin));
    }
}
