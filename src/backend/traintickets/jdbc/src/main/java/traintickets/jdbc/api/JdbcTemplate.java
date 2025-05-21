package traintickets.jdbc.api;

public interface JdbcTemplate {
    <T> T executeFunc(int isolation, TransactionFunction<T> function);
    void executeCons(int isolation, TransactionConsumer consumer);
    void close();
}
