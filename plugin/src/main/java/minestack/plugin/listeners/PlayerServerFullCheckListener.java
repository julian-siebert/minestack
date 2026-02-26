package minestack.plugin.listeners;

import com.google.inject.Singleton;
import io.papermc.paper.event.player.PlayerServerFullCheckEvent;
import minestack.plugin.MinestackPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

@Singleton
public final class PlayerServerFullCheckListener implements Listener {
    private final MinestackPlugin plugin;

    public PlayerServerFullCheckListener(MinestackPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerServerFullCheckEvent(PlayerServerFullCheckEvent event) {
        var uniqueId = event.getPlayerProfile().getId();

    }
}
