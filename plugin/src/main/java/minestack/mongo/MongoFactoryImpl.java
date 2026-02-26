package minestack.mongo;

import com.google.inject.Inject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.key.Key;
import org.bson.Document;
import org.bukkit.plugin.Plugin;

@RequiredArgsConstructor
public final class MongoFactoryImpl implements MongoFactory {
    private final MongoCodecRegistry registry = new MongoCodecRegistry();

    private final MongoDatabase database;

    @SuppressWarnings("PatternValidation")
    @Override
    public <T extends Record> MongoCollection<T> collection(@NonNull Plugin plugin, @NonNull String name, @NonNull MongoCodec<T> codec) {
        var key = Key.key(plugin.namespace(), name);
        synchronized (this) {
            registry.introduce(new MongoCodecAdapter<>(codec));
            return database.getCollection(key.asString(), codec.clazz()).withCodecRegistry(registry);
        }
    }

    @SuppressWarnings("PatternValidation")
    @Override
    public MongoCollection<Document> collection(@NonNull Plugin plugin, @NonNull String name) {
        var key = Key.key(plugin.namespace(), name);
        synchronized (this) {
            return database.getCollection(key.asString()).withCodecRegistry(registry);
        }
    }
}
