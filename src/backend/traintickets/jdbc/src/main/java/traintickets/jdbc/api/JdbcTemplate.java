package traintickets.jdbc.api;

public interface JdbcTemplate {
    <T> T executeFunc(String role, int isolation, TransactionFunction<T> function);
    void executeCons(String role, int isolation, TransactionConsumer consumer);
    void close();
}
