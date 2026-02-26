package minestack.mongo;

import minestack.Error;
import org.jetbrains.annotations.NotNull;

public record ClassCastError(ClassCastException ex) implements MongoCodecError {
    @Override
    public @NotNull Exception exception() {
        return ex;
    }
}
