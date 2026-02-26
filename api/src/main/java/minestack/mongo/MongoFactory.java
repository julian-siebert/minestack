package minestack.mongo;

import com.mongodb.client.MongoCollection;
import lombok.NonNull;
import net.kyori.adventure.key.KeyPattern;
import org.bson.Document;
import org.bukkit.plugin.Plugin;

public interface MongoFactory {

    <T extends Record> MongoCollection<T> collection(@NonNull Plugin plugin, @NonNull @KeyPattern.Value String name, @NonNull MongoCodec<T> codec);

    MongoCollection<Document> collection(@NonNull Plugin plugin, @NonNull @KeyPattern.Value String name);

}
