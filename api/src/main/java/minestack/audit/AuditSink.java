package minestack.audit;

import lombok.NonNull;
import minestack.actions.Actor;

import java.time.Instant;
import java.util.UUID;

public interface AuditSink {

    AuditSink field(@NonNull String name, @NonNull Record record);

    AuditSink field(@NonNull String name, @NonNull Actor actor);

    AuditSink field(@NonNull String name, @NonNull UUID uniqueId);

    AuditSink field(@NonNull String name, @NonNull String string);

    AuditSink field(@NonNull String name, @NonNull Number number);

    AuditSink field(@NonNull String name, @NonNull Instant instant);
}
