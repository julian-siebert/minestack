package minestack.builtin.player;

import minestack.mongo.ClassCastError;
import minestack.mongo.MongoCodec;
import minestack.mongo.MongoCodecError;
import minestack.result.Result;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.UUID;

public final class PlayerActorCodec implements MongoCodec<PlayerActor> {

    @Override
    public Result<Document, MongoCodecError> encode(@NonNull PlayerActor actor) {
        var doc = new Document();
        var player = actor.player();
        doc.put("uniqueId", player.getUniqueId());
        return Result.value(doc);
    }

    @Override
    public Result<PlayerActor, MongoCodecError> decode(@NotNull Document document) {
        try {
            var uniqueId = document.get("uniqueId", UUID.class);
            var player = Bukkit.getOfflinePlayer(uniqueId);
            return Result.value(new PlayerActor(player));
        } catch (ClassCastException exception) {
            return Result.error(new ClassCastError(exception));
        }
    }

    @Override
    public Class<PlayerActor> clazz() {
        return PlayerActor.class;
    }
}
