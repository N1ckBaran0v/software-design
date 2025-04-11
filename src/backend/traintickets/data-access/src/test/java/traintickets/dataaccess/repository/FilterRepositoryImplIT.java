//package traintickets.dataaccess.repository;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import traintickets.businesslogic.exception.EntityAlreadyExistsException;
//import traintickets.businesslogic.model.Filter;
//import traintickets.businesslogic.model.UserId;
//import traintickets.businesslogic.repository.FilterRepository;
//
//
//import java.sql.Connection;
//import java.util.Map;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class FilterRepositoryImplIT extends PostgresIT {
//    private FilterRepository filterRepository;
//
//    @BeforeEach
//    @Override
//    public void setUp() {
//        super.setUp();
//        filterRepository = new FilterRepositoryImpl(jdbcTemplate);
//    }
//
//    @Override
//    protected void insertData() {
//        jdbcTemplate.executeCons(roleName, Connection.TRANSACTION_READ_UNCOMMITTED, connection -> {
//            try (var statement = connection.prepareStatement(
//                    "insert into users_view (user_name, pass_word, real_name, user_role, is_active) values " +
//                            "('first', 'qwerty123', 'Иванов Иван Иванович', 'userRole', TRUE), " +
//                            "('second', 'qwerty123', 'Петров Пётр Петрович', 'userRole', TRUE); " +
//                            "insert into filters (user_id, filter_name, departure, destination, transfers) values " +
//                            "(1, 'first', 'first', 'second', 0), " +
//                            "(1, 'second', 'first', 'second', 1); " +
//                            "insert into passengers (filter_id, passengers_type, passengers_count) values " +
//                            "(1, 'adult', 2), (1, 'child', 1), (2, 'adult', 1); "
//            )) {
//                statement.execute();
//            }
//        });
//    }
//
//    @Test
//    void addFilter_positive_added() {
//        var filter = new Filter(new UserId(2L), "first", "first", "second", 0, Map.of("adult", 1), null, null);
//        filterRepository.addFilter(roleName, filter);
//        jdbcTemplate.executeCons(roleName, Connection.TRANSACTION_READ_UNCOMMITTED, connection -> {
//            try (var statement = connection.prepareStatement(
//                    "SELECT * FROM filters WHERE id = 3;"
//            )) {
//                try (var resultSet = statement.executeQuery()) {
//                    assertTrue(resultSet.next());
//                    assertEquals(filter.user().id(), resultSet.getLong("user_id"));
//                    assertEquals("first", resultSet.getString("filter_name"));
//                    assertEquals("first", resultSet.getString("departure"));
//                    assertEquals("second", resultSet.getString("destination"));
//                    assertEquals(0, resultSet.getInt("transfers"));
//                    assertFalse(resultSet.next());
//                }
//            }
//            try (var statement = connection.prepareStatement(
//                    "SELECT * FROM passengers WHERE filter_id = 3;"
//            )) {
//                try (var resultSet = statement.executeQuery()) {
//                    assertTrue(resultSet.next());
//                    assertEquals("adult", resultSet.getString("passengers_type"));
//                    assertEquals(1, resultSet.getInt("passengers_count"));
//                    assertFalse(resultSet.next());
//                }
//            }
//        });
//    }
//
//    @Test
//    void addFilter_negative_exists() {
//        var filter = new Filter(new UserId(1L), "first", "first", "second", 0, Map.of("adult", 1), null, null);
//        assertThrows(EntityAlreadyExistsException.class, () -> filterRepository.addFilter(roleName, filter));
//        jdbcTemplate.executeCons(roleName, Connection.TRANSACTION_READ_UNCOMMITTED, connection -> {
//            try (var statement = connection.prepareStatement(
//                    "SELECT * FROM filters WHERE id = 3;"
//            )) {
//                try (var resultSet = statement.executeQuery()) {
//                    assertFalse(resultSet.next());
//                }
//            }
//            try (var statement = connection.prepareStatement(
//                    "SELECT * FROM passengers WHERE filter_id = 3;"
//            )) {
//                try (var resultSet = statement.executeQuery()) {
//                    assertFalse(resultSet.next());
//                }
//            }
//        });
//    }
//
//    @Test
//    void getFilter_positive_found() {
//        var filter = new Filter(new UserId(1L), "first", "first", "second", 0, Map.of("adult", 2, "child", 1), null, null);
//        var result = filterRepository.getFilter(roleName, new UserId(1), "first");
//        assertEquals(filter, result.orElse(null));
//    }
//
//    @Test
//    void getFilter_positive_notFound() {
//        assertNull(filterRepository.getFilter(roleName, new UserId(1L), "third").orElse(null));
//    }
//
//    @Test
//    void getFilters_positive_got() {
//        var filter1 = new Filter(new UserId(1L), "first", "first", "second", 0, Map.of("adult", 2, "child", 1), null, null);
//        var filter2 = new Filter(new UserId(1L), "second", "first", "second", 1, Map.of("adult", 1), null, null);
//        var result = filterRepository.getFilters(roleName, new UserId(1L));
//        assertNotNull(result);
//        var iterator = result.iterator();
//        assertTrue(iterator.hasNext());
//        assertEquals(filter1, iterator.next());
//        assertTrue(iterator.hasNext());
//        assertEquals(filter2, iterator.next());
//        assertFalse(iterator.hasNext());
//    }
//
//    @Test
//    void getFilters_positive_empty() {
//        var result = filterRepository.getFilters(roleName, new UserId(2L));
//        assertNotNull(result);
//        assertFalse(result.iterator().hasNext());
//    }
//
//    @Test
//    void deleteFilter_positive_deleted() {
//        filterRepository.deleteFilter(roleName, new UserId(1L), "first");
//        jdbcTemplate.executeCons(roleName, Connection.TRANSACTION_READ_UNCOMMITTED, connection -> {
//            try (var statement = connection.prepareStatement(
//                    "SELECT * FROM filters WHERE id = 1;"
//            )) {
//                try (var resultSet = statement.executeQuery()) {
//                    assertFalse(resultSet.next());
//                }
//            }
//        });
//    }
//}