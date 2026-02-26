package minestack.either;

import org.jetbrains.annotations.NotNull;

public sealed interface Either<L, R> permits Left, Right {

    default boolean isLeft() {
        return this instanceof Left<R,L>;
    }

    default boolean isRight() {
        return this instanceof Right<R,L>;
    }

    default Either<R, L> swap() {
        return switch (this) {
            case Left<R, L> v -> right(v.left());
            case Right<R, L> v -> left(v.right());
        };
    }

    static <L, R> Either<L, R> left(@NotNull L left) {
        return new Left<>(left);
    }

    static <L, R> Either<L, R> right(@NotNull R right) {
        return new Right<>(right);
    }
}
