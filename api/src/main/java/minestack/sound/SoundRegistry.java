package minestack.sound;

import lombok.NonNull;
import minestack.option.Option;
import minestack.registry.Registry;
import net.kyori.adventure.key.KeyPattern;
import net.kyori.adventure.sound.Sound;
import org.bukkit.plugin.Plugin;

public interface SoundRegistry extends Registry {

    void registerSound(@NonNull Plugin plugin, @NonNull @KeyPattern.Value String name, @NonNull @SoundPattern String file);

    Option<Sound> sound(@NonNull Plugin plugin, @NonNull @KeyPattern.Value String name);

}
