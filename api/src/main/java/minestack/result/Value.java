package minestack.result;

import minestack.Error;
import org.jetbrains.annotations.NotNull;

public record Value<V, E extends Error>(@NotNull V value) implements Result<V, E> {

}
