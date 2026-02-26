package minestack.plugin;

import minestack.Minestack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@ApiStatus.Internal
public final class Internal {
    private static final CompletableFuture<Minestack> FUTURE_INSTANCE = new CompletableFuture<>();

    @ApiStatus.Internal
    static void init(@NotNull Minestack instance) {
        FUTURE_INSTANCE.complete(instance);
    }

    public static Minestack instance() {
        try {
            return FUTURE_INSTANCE.get(10, TimeUnit.SECONDS);
        } catch (ExecutionException e) {
            throw new RuntimeException("Minestack api initialization throw an exception", e);
        } catch (InterruptedException e) {
            throw new RuntimeException("Minestack api initialization interrupted", e);
        } catch (TimeoutException e) {
            throw new IllegalStateException("Minestack api could not be initialized (timed out)", e);
        }
    }
}
