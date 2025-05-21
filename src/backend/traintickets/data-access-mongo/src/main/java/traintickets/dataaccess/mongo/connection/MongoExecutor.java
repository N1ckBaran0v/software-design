package traintickets.dataaccess.mongo.connection;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public final class MongoExecutor {
    private final MongoClient mongoClient;
    private final MongoDatabase mongoDatabase;

    public MongoExecutor(MongoConfig mongoConfig) {
        mongoClient = MongoClients.create(getConnectionString(mongoConfig));
        mongoDatabase = mongoClient.getDatabase(mongoConfig.database());
    }

    private String getConnectionString(MongoConfig config) {
        var connectionString = "";
        if (config.username() == null) {
            connectionString = String.format("mongodb://%s:%d/%s?maxPoolSize=%d",
                    config.host(), config.port(), config.database(), config.poolSize());
        } else {
            connectionString = String.format("mongodb://%s:%s@%s:%d/%s?maxPoolSize=%d",
                    config.username(), config.password(),
                    config.host(), config.port(), config.database(), config.poolSize());
        }
        if (config.replicaSet() != null) {
            connectionString = String.format("%s&replicaSet=%s", connectionString, config.replicaSet());
        }
        return connectionString;
    }

    public <T> T executeFunction(TransactionFunction<T> function) {
        try (var session = mongoClient.startSession()) {
            return function.apply(session);
        }
    }

    public void executeConsumer(TransactionConsumer consumer) {
        try (var session = mongoClient.startSession()) {
            consumer.accept(session);
        }
    }

    public <T> T transactionFunction(TransactionFunction<T> function) {
        try (var session = mongoClient.startSession()) {
            session.startTransaction();
            try {
                var result = function.apply(session);
                session.commitTransaction();
                return result;
            } catch (RuntimeException e) {
                session.abortTransaction();
                throw e;
            }
        }
    }

    public void transactionConsumer(TransactionConsumer consumer) {
        try (var session = mongoClient.startSession()) {
            session.startTransaction();
            try {
                consumer.accept(session);
                session.commitTransaction();
            } catch (RuntimeException e) {
                session.abortTransaction();
                throw e;
            }
        }
    }

    public MongoDatabase getDatabase() {
        return mongoDatabase;
    }
}
