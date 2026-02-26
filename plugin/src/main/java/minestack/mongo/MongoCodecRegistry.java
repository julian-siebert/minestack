package minestack.mongo;

import com.mongodb.MongoClientSettings;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import lombok.NonNull;
import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecRegistry;

import java.util.Map;

public final class MongoCodecRegistry implements CodecRegistry {
    private final Map<Class<?>, Codec<?>> codecs = new Object2ObjectArrayMap<>();
    private final CodecRegistry fallback = MongoClientSettings.getDefaultCodecRegistry();

    public void introduce(@NonNull Codec<?> codec) {
        synchronized (codecs) {
            codecs.put(codec.getEncoderClass(), codec);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Codec<T> get(Class<T> clazz) {
        synchronized (codecs) {
            Codec<?> codec = codecs.get(clazz);
            if (codec != null) {
                return (Codec<T>) codec;
            }
        }
        return fallback.get(clazz);
    }

    @Override
    public <T> Codec<T> get(Class<T> clazz, CodecRegistry registry) {
        return get(clazz);
    }
}
