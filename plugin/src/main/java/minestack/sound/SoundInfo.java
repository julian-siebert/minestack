package minestack.sound;

import lombok.NonNull;
import org.bukkit.plugin.Plugin;

public record SoundInfo(@NonNull Plugin plugin, @NonNull String name, @NonNull String file) {

}
