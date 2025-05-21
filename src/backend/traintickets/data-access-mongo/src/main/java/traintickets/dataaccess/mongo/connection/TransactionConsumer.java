package traintickets.dataaccess.mongo.connection;

import com.mongodb.client.ClientSession;

@FunctionalInterface
public interface TransactionConsumer {
    void accept(ClientSession session);
}
