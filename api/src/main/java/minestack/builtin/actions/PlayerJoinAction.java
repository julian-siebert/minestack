package minestack.builtin.actions;

import lombok.NonNull;
import minestack.actions.Action;
import minestack.actions.Actor;
import minestack.audit.AuditSink;
import minestack.builtin.player.PlayerActor;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public record PlayerJoinAction(@NonNull PlayerActor player) implements Action {

    @Override
    public Collection<? extends Actor> actors() {
        return List.of(player);
    }

    @Override
    public void audit(@NotNull AuditSink sink) {

    }
}
