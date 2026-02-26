package minestack.result;

import org.jetbrains.annotations.NotNull;

public sealed interface Result<V, E extends minestack.Error> permits Value, Error {

    default boolean isError() {
        return this instanceof Error<V, E>;
    }

    static <V, E extends minestack.Error> Result<V, E> value(@NotNull V value) {
        return new Value<>(value);
    }

    static <V, E extends minestack.Error> Result<V, E> error(E err) {
        return new Error<>(err);
    }
}
