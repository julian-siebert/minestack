package minestack.option;

import minestack.Error;
import minestack.result.Result;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

public sealed interface Option<V> permits Some, None {
    
    default Optional<V> optional() {
        return switch (this) {
            case None<V> _ -> Optional.empty();
            case Some<V> v -> Optional.of(v.value());
        };
    }
    
    default V get() throws NoSuchElementException {
        return switch (this) {
            case None<V> v -> throw new NoSuchElementException("No value resent.");
            case Some<V> v -> v.value();
        };
    }

    default boolean isPresent() {
        return isSome();
    }

    default boolean isSome() {
        return this instanceof Some<V>;
    }

    default boolean isEmpty() {
        return isNone();
    }

    default boolean isNone() {
        return this instanceof None<V>;
    }

    default void ifPresent(@NotNull Consumer<? super V> action) {
        if (this instanceof Some<V>(V value)) {
            action.accept(value);
        }
    }

    default Option<V> filter(@NotNull Predicate<? super V> predicate) {
        return switch (this) {
            case None<V> v -> v;
            case Some<V> v -> predicate.test(v.value()) ? this : none();
        };
    }

    default <U> Option<U> map(@NotNull Function<? super V, ? extends U> function) {
        return switch (this) {
            case None<V> _ -> none();
            case Some<V> v -> maybe(function.apply(v.value()));
        };
    }

    default <U> Option<U> flatMap(@NotNull Function<? super V, ? extends Option<U>> function) {
        return switch (this) {
            case None<V> _ -> none();
            case Some<V> v -> function.apply(v.value());
        };
    }

    default Option<V> or(@NotNull Supplier<? extends Option<V>> supplier) {
        return switch (this) {
            case None<V> _ -> supplier.get();
            case Some<V> v -> v;
        };
    }

    @NotNull
    default V orElse(@NotNull V other) {
        return switch (this) {
            case None<V> _ -> other;
            case Some<V> v -> v.value();
        };
    }

    default <E extends Record & Error> Result<V, E> result(@NotNull Supplier<E> supplier) {
        return switch (this) {
            case None<V> _ -> Result.error(supplier.get());
            case Some<V> v -> Result.value(v.value());
        };
    }

    default Stream<V> stream() {
        return switch (this) {
            case None<V> _ -> Stream.empty();
            case Some<V> v -> Stream.of(v.value());
        };
    }

    static <V> Some<V> some(@NotNull V value) {
        return new Some<>(value);
    }

    static <V> Some<V> of(@NotNull V value) {
        return new Some<>(value);
    }

    static <V> None<V> none() {
        return new None<>();
    }

    static <V> None<V> empty() {
        return new None<>();
    }

    static <V> Option<V> maybe(@Nullable V value) {
        if (value == null) return none();
        return some(value);
    }

    static <V> Option<V> ofNullable(@Nullable V value) {
        if (value == null) return none();
        return some(value);
    }
}
