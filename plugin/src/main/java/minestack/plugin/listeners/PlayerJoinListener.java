package minestack.plugin.listeners;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import minestack.builtin.actions.PlayerJoinAction;
import minestack.builtin.player.PlayerActor;
import minestack.plugin.MinestackPlugin;
import minestack.plugin.permission.PermissibleImpl;
import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;

@Singleton
public final class PlayerJoinListener implements Listener {
    private final MinestackPlugin minestack;

    @Inject
    public PlayerJoinListener(MinestackPlugin plugin) {
        this.minestack = plugin;
    }

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        event.joinMessage(null);

        var player = event.getPlayer();
        var actor = new PlayerActor(player);
        try {
            PermissibleImpl.hookPlayer(player, new PermissibleImpl(actor));
        } catch (Exception exception) {
            minestack.getSLF4JLogger().error("Cannot hook Permissible to player {}", player.getUniqueId(), exception);
            player.kick(Component.text("Internal server error!"), PlayerKickEvent.Cause.UNKNOWN);
        }
        minestack.trigger(minestack, new PlayerJoinAction(actor));
    }
}
