package minestack.plugin;

import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.PluginLoader;
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;
import org.jspecify.annotations.NonNull;

@SuppressWarnings("UnstableApiUsage")
public final class MinestackPluginLoader implements PluginLoader {
    @Override
    public void classloader(@NonNull PluginClasspathBuilder builder) {
        var resolver = new MavenLibraryResolver();
        for (var artifact : Constants.PLUGIN_LIBRARIES.split(",")) {
            resolver.addDependency(new Dependency(new DefaultArtifact(artifact), null));
        }
        resolver.addRepository(new RemoteRepository.Builder("central", "default", MavenLibraryResolver.MAVEN_CENTRAL_DEFAULT_MIRROR).build());
        builder.addLibrary(resolver);
    }
}
