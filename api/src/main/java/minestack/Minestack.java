package minestack;

import com.google.inject.Injector;
import lombok.NonNull;
import minestack.actions.Action;
import minestack.option.Option;
import minestack.permission.PermissionRegistry;
import minestack.plugin.Internal;
import minestack.registry.Registry;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.KeyPattern;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface Minestack extends Plugin {

    Option<MinecraftEdition> edition(@NonNull OfflinePlayer player);

    <T extends Record & Action> void trigger(@NotNull Plugin plugin, T action);

    <T extends Record & Action> CompletableFuture<Void> triggerAsync(@NotNull Plugin plugin, T action);

    <T extends Listener> T register(@NonNull Plugin plugin, @NonNull Class<T> listener);


    <T extends Registry> T registry(Class<T> registryClass);

    PermissionRegistry permissionRegistry();


    <T> Optional<T> config(@NotNull Plugin plugin, @NotNull String name, @NotNull Class<T> tClass);


    <T> @NotNull T config(@NotNull Plugin plugin, @NotNull String name, @NotNull Class<T> tClass, @NotNull T defaultConfig);

    Injector injector();

    @SuppressWarnings("PatternValidation")
    default @NotNull Key key(@NotNull Plugin plugin, @NotNull @KeyPattern.Value String value) {
        return Key.key(plugin.namespace(), value.toLowerCase(Locale.ROOT));
    }

    @NotNull
    static Minestack api() {
        return Internal.instance();
    }
}
