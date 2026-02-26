package minestack.plugin.listeners;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import minestack.builtin.actions.PlayerQuitAction;
import minestack.builtin.player.PlayerActor;
import minestack.plugin.MinestackPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

@Singleton
public final class PlayerQuitListener implements Listener {
    private final MinestackPlugin minestack;

    @Inject
    public PlayerQuitListener(MinestackPlugin plugin) {
        this.minestack = plugin;
    }

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        event.quitMessage(null);
        var actor = new PlayerActor(event.getPlayer());
        minestack.trigger(minestack, new PlayerQuitAction(actor));
    }
}
