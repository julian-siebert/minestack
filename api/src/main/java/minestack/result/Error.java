package minestack.result;

import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

public record Error<V, E extends minestack.Error>(@NotNull E error) implements Result<V, E>, minestack.Error {

    @Override
    public @NotNull Exception exception() {
        return error.exception();
    }

    @Override
    public @NonNull String toString() {
        return exception().toString();
    }

    @Override
    public int hashCode() {
        return exception().hashCode();
    }
}
