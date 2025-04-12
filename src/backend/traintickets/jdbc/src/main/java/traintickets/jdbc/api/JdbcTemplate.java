package traintickets.jdbc.api;

public interface JdbcTemplate extends AutoCloseable {
    <T> T executeFunc(String username, int isolation, TransactionFunction<T> function);
    void executeCons(String username, int isolation, TransactionConsumer consumer);
}
