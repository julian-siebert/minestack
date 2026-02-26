package minestack.either;

import org.jetbrains.annotations.NotNull;

public record Right<R, L>(@NotNull R right) implements Either<L, R> {

}
