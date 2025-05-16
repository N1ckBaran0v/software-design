package traintickets.dataaccess.mongo.connection;

import com.mongodb.client.MongoDatabase;

public final class MongoExecutor {
    private final MongoPool mongoPool;

    public MongoExecutor(MongoConfig mongoConfig) {
        this.mongoPool = new MongoPool(mongoConfig);
    }

    public <T> T executeFunction(TransactionFunction<T> function) {
        var session = mongoPool.getSession();
        try {
            return function.apply(session);
        } finally {
            mongoPool.releaseSession(session);
        }
    }

    public void executeConsumer(TransactionConsumer consumer) {
        var session = mongoPool.getSession();
        try {
            consumer.accept(session);
        } finally {
            mongoPool.releaseSession(session);
        }
    }

    public <T> T transactionFunction(TransactionFunction<T> function) {
        var session = mongoPool.getSession();
        session.startTransaction();
        try {
            var result = function.apply(session);
            session.commitTransaction();
            return result;
        } catch (RuntimeException e) {
            session.abortTransaction();
            throw e;
        } finally {
            mongoPool.releaseSession(session);
        }
    }

    public void transactionConsumer(TransactionConsumer consumer) {
        var session = mongoPool.getSession();
        session.startTransaction();
        try {
            consumer.accept(session);
            session.commitTransaction();
        } catch (RuntimeException e) {
            session.abortTransaction();
            throw e;
        } finally {
            mongoPool.releaseSession(session);
        }
    }

    public MongoDatabase getDatabase() {
        return mongoPool.getDatabase();
    }
}
