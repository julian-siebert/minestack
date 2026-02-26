package minestack;

import org.jetbrains.annotations.NotNull;

public interface Error {

    @NotNull
    Exception exception();

    default String message() {
        return exception().getMessage();
    }

    default String localizedMessage() {
        return exception().getLocalizedMessage();
    }

    default Throwable cause() {
        return exception().getCause();
    }

    default Throwable[] suppressed() {
        return exception().getSuppressed();
    }

    default StackTraceElement[] stacktrace() {
        return exception().getStackTrace();
    }
}
