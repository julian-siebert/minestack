package minestack.audit;

import org.jetbrains.annotations.NotNull;

public interface Audible {

    void audit(@NotNull AuditSink sink);

}
