package traintickets.dataaccess.mongo.connection;

public record MongoConfig(String host, int port, String database, String username, String password, int poolSize) {
}
