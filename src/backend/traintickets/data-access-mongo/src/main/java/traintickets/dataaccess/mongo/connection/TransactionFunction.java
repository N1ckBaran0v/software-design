package traintickets.dataaccess.mongo.connection;

import com.mongodb.client.ClientSession;

@FunctionalInterface
public interface TransactionFunction<T> {
    T apply(ClientSession session);
}
