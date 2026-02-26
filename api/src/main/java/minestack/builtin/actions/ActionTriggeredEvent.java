package minestack.builtin.actions;

import lombok.NonNull;
import minestack.actions.Action;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public final class ActionTriggeredEvent extends Event {
    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final Action action;

    public ActionTriggeredEvent(@NonNull Action action) {
        super(true);
        this.action = action;
    }

    public Action action() {
        return action;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
