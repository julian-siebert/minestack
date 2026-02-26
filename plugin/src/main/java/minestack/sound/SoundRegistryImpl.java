package minestack.sound;

import com.google.inject.Inject;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import lombok.NonNull;
import minestack.option.Option;
import minestack.plugin.MinestackPlugin;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.plugin.Plugin;

import java.util.Map;

public final class SoundRegistryImpl implements SoundRegistry {
    private final Map<Key, SoundInfo> sounds = new Object2ObjectArrayMap<>();

    private final MinestackPlugin plugin;

    @Inject
    public SoundRegistryImpl(MinestackPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void registerSound(@NonNull Plugin plugin, @NonNull String name, @NonNull String file) {
        synchronized (sounds) {
            var info = new SoundInfo(plugin, name, file);
            sounds.put(this.plugin.key(plugin, name), info);
        }
    }

    @Override
    public Option<Sound> sound(@NonNull Plugin plugin, @NonNull String name) {
        return null;
    }
}
