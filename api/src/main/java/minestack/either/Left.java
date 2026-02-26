package minestack.either;

import org.jetbrains.annotations.NotNull;

public record Left<R, L>(@NotNull L left) implements Either<L, R> {

}
