package minestack.builtin.player;

import lombok.NonNull;
import minestack.MinecraftEdition;
import minestack.Minestack;
import minestack.actions.Actor;
import minestack.adventure.Permissible;
import minestack.option.Option;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.sound.SoundStop;
import net.kyori.adventure.util.TriState;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

public record PlayerActor(@NonNull OfflinePlayer player) implements Actor, Audience, Permissible {

    @Override
    public void playSound(@NotNull Sound sound) {
        online().ifPresent(player -> player.playSound(sound));
    }

    @Override
    public void playSound(@NotNull Sound sound, Sound.@NotNull Emitter emitter) {
        online().ifPresent(player -> player.playSound(sound, emitter));
    }

    @Override
    public void stopSound(@NotNull Sound sound) {
        online().ifPresent(player -> player.stopSound(sound));
    }

    @Override
    public void stopSound(@NotNull SoundStop stop) {
        online().ifPresent(player -> player.stopSound(stop));
    }

    @Override
    public void playSound(@NotNull Sound sound, double x, double y, double z) {
        online().ifPresent(player -> player.playSound(sound, x, y, z));
    }

    public Option<MinecraftEdition> edition() {
        return Minestack.api().edition(player);
    }

    public Option<Player> online() {
        return Option.maybe(Bukkit.getPlayer(player.getUniqueId()));
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        PlayerActor that = (PlayerActor) object;
        return Objects.equals(player, that.player);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(player);
    }

    @Override
    public TriState permission(@NonNull String node) {
        return Minestack.api().permissionRegistry().permission(uniqueId(), node);
    }

    public UUID uniqueId() {
        return player.getUniqueId();
    }
}
