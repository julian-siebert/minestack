package minestack.mongo;

import com.google.inject.AbstractModule;
import com.mongodb.client.MongoDatabase;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class MongoFactoryModule extends AbstractModule {
    private final MongoDatabase database;

    @Override
    protected void configure() {
        bind(MongoFactory.class).toInstance(new MongoFactoryImpl(database));
    }
}
