package minestack.mongo;

import lombok.NonNull;
import minestack.result.Result;
import org.bson.Document;

public interface MongoCodec<T extends Record> {

    Result<Document, MongoCodecError> encode(@NonNull T record);

    Result<T, MongoCodecError> decode(@NonNull Document document);

    Class<T> clazz();
}
