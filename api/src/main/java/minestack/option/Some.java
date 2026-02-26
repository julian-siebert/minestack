package minestack.option;

import org.jetbrains.annotations.NotNull;

public record Some<V>(@NotNull V value) implements Option<V> {

}
