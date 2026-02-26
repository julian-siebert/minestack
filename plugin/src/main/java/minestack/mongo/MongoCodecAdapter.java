package minestack.mongo;

import lombok.RequiredArgsConstructor;
import minestack.result.Error;
import minestack.result.Value;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.Document;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.DocumentCodec;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecConfigurationException;

@RequiredArgsConstructor
public final class MongoCodecAdapter<R extends Record> implements Codec<R> {
    private final MongoCodec<R> codec;

    @Override
    public R decode(BsonReader reader, DecoderContext context) {
        var documentCodec = new DocumentCodec();
        var doc = documentCodec.decode(reader, context);
        return switch (codec.decode(doc)) {
            case Error<R, MongoCodecError> v -> throw new CodecConfigurationException("Decode failed", v.exception());
            case Value<R, MongoCodecError> v -> v.value();
        };
    }

    @Override
    public void encode(BsonWriter writer, R value, EncoderContext context) {
        switch (codec.encode(value)) {
            case Error<Document, MongoCodecError> v -> throw new CodecConfigurationException("Encode failed", v.exception());
            case Value<Document, MongoCodecError> v -> {
                var documentCodec = new DocumentCodec();
                documentCodec.encode(writer, v.value(), context);
            }
        }
    }

    @Override
    public Class<R> getEncoderClass() {
        return codec.clazz();
    }
}
