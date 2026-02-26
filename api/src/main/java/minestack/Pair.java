package minestack;

import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

public record Pair<L, R>(@NotNull L left, @NotNull R right) {

    public Pair<R, L> swap() {
        return new Pair<>(right, left);
    }

    public <U> Pair<U, R> mapLeft(@NotNull Function<? super L, ? extends U> function) {
        return new Pair<>(function.apply(left), right);
    }

    public <V> Pair<L, V> mapRight(@NotNull Function<? super R, ? extends V> function) {
        return new Pair<>(left, function.apply(right));
    }

    public <U, V> Pair<U, V> map(@NotNull Function<? super L, ? extends U> left,
                                 @NotNull Function<? super R, ? extends V> right) {
        return new Pair<>(left.apply(this.left), right.apply(this.right));
    }

    public Stream<Pair<L, R>> stream() {
        return Stream.of(this);
    }

    public static <L, R> Pair<L, R> of(@NotNull L left, @NotNull R right) {
        return new Pair<>(left, right);
    }

    public static <A, B> Stream<Pair<A, B>> zip(@NotNull Stream<A> a, @NotNull Stream<B> b) {
        var itA = a.iterator();
        var itB = b.iterator();
        Stream.Builder<Pair<A, B>> builder = Stream.builder();
        while (itA.hasNext() && itB.hasNext())
            builder.add(new Pair<>(itA.next(), itB.next()));
        return builder.build();
    }

    @Override
    public @NonNull String toString() {
        return "Pair[left=" + left + ", right=" + right + "]";
    }

    @Override
    public int hashCode() {
        return Objects.hash(left, right);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        return obj instanceof Pair<?,?>(Object left, Object right)
                && Objects.equals(this.left, left)
                && Objects.equals(this.right, right);
    }
}
