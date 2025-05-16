package traintickets.dataaccess.mongo.connection;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

final class MongoPool implements AutoCloseable {
    private final MongoClient mongoClient;
    private final List<ClientSession> pool, sessions;
    private final Semaphore semaphore;
    private final MongoDatabase database;

    public MongoPool(MongoConfig config) {
        var connectionString = getConnectionString(config);
        mongoClient = MongoClients.create(connectionString);
        database = mongoClient.getDatabase(config.database());
        pool = new ArrayList<>();
        for (var i = 0; i < config.poolSize(); ++i) {
            var session = mongoClient.startSession();
            pool.add(session);
        }
        sessions = List.copyOf(pool);
        semaphore = new Semaphore(config.poolSize());
    }

    private static String getConnectionString(MongoConfig config) {
        var connectionString = "";
        if (config.username() == null) {
            connectionString = String.format("mongodb://%s:%d/%s?maxPoolStze=%d",
                    config.host(), config.port(), config.database(), config.poolSize());
        } else {
            connectionString = String.format("mongodb://%s:%s@%s:%d/%s?maxPoolStze=%d",
                    config.username(), config.password(),
                    config.host(), config.port(), config.database(), config.poolSize());
        }
        return connectionString;
    }

    ClientSession getSession() {
        try {
            semaphore.acquire();
            synchronized (pool) {
                return pool.removeLast();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    void releaseSession(ClientSession session) {
        synchronized (pool) {
            pool.add(session);
            semaphore.release();
        }
    }

    @Override
    public void close() {
        sessions.forEach(ClientSession::close);
        mongoClient.close();
    }

    public MongoDatabase getDatabase() {
        return database;
    }
}
