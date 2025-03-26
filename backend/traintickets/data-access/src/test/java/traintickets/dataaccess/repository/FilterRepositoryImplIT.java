package traintickets.dataaccess.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import traintickets.businesslogic.exception.FilterAlreadyExistsException;
import traintickets.businesslogic.model.Filter;
import traintickets.businesslogic.model.UserId;
import traintickets.businesslogic.repository.FilterRepository;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FilterRepositoryImplIT extends PostgresIT {
    private FilterRepository filterRepository;

    @BeforeEach
    @Override
    public void setUp() {
        super.setUp();
        filterRepository = new FilterRepositoryImpl(jdbcTemplate, roleName);
    }

    @Test
    void addFilter_positive_added() {
        var filter = new Filter(new UserId(2), "first", "first", "second", "Экспресс", 0,
                List.of("adult"), null, null, BigDecimal.TEN, BigDecimal.valueOf(10000));
        filterRepository.addFilter(filter);
        jdbcTemplate.executeCons(roleName, Connection.TRANSACTION_READ_UNCOMMITTED, conn -> {
            try (var statement = conn.prepareStatement(
                    "SELECT * FROM filters WHERE id = 3;"
            )) {
                try (var resultSet = statement.executeQuery()) {
                    assertTrue(resultSet.next());
                    assertEquals(2, resultSet.getLong(2));
                    assertEquals("first", resultSet.getString(3));
                    assertEquals("first", resultSet.getString(4));
                    assertEquals("second", resultSet.getString(5));
                    assertEquals("Экспресс", resultSet.getString(6));
                    assertEquals(0, resultSet.getInt(7));
                    assertEquals(BigDecimal.TEN, resultSet.getBigDecimal(8));
                    assertEquals(BigDecimal.valueOf(10000), resultSet.getBigDecimal(9));
                    assertFalse(resultSet.next());
                }
            }
            try (var statement = conn.prepareStatement(
                    "SELECT * FROM passengers WHERE filter_id = 3;"
            )) {
                try (var resultSet = statement.executeQuery()) {
                    assertTrue(resultSet.next());
                    assertEquals("adult", resultSet.getString(3));
                    assertEquals(1, resultSet.getInt(4));
                    assertFalse(resultSet.next());
                }
            }
        });
    }

    @Test
    void addFilter_negative_exists() {
        var filter = new Filter(new UserId(1), "first", "first", "second", "Экспресс", 0,
                List.of("adult"), null, null, BigDecimal.TEN, BigDecimal.valueOf(10000));
        assertThrows(FilterAlreadyExistsException.class, () -> filterRepository.addFilter(filter));
    }

    @Test
    void getFilter_positive_found() {
        var filter = new Filter(new UserId(1), "first", "first", "second", "Экспресс", 0,
                List.of("adult", "adult", "child"), null, null, BigDecimal.valueOf(100), BigDecimal.valueOf(10000));
        var result = filterRepository.getFilter(new UserId(1), "first");
        assertEquals(filter, result.orElse(null));
    }

    @Test
    void getFilter_positive_notFound() {
        assertNull(filterRepository.getFilter(new UserId(1), "third").orElse(null));
    }

    @Test
    void getFilters_positive_got() {
        var filter1 = new Filter(new UserId(1), "first", "first", "second", "Экспресс", 0,
                List.of("adult", "adult", "child"), null, null, BigDecimal.valueOf(100), BigDecimal.valueOf(10000));
        var filter2 = new Filter(new UserId(1), "second", "first", "second", "Скорый", 1,
                List.of("adult"), null, null, BigDecimal.valueOf(100), BigDecimal.valueOf(10000));
        var result = filterRepository.getFilters(new UserId(1));
        assertNotNull(result);
        var iterator = result.iterator();
        assertTrue(iterator.hasNext());
        assertEquals(filter1, iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals(filter2, iterator.next());
        assertFalse(iterator.hasNext());
    }

    @Test
    void getFilters_positive_empty() {
        var result = filterRepository.getFilters(new UserId(2));
        assertNotNull(result);
        assertFalse(result.iterator().hasNext());
    }

    @Test
    void deleteFilter_positive_deleted() {
        filterRepository.deleteFilter(new UserId(1), "first");
        jdbcTemplate.executeCons(roleName, Connection.TRANSACTION_READ_UNCOMMITTED, conn -> {
            try (var statement = conn.prepareStatement(
                    "SELECT * FROM filters WHERE id = 1;"
            )) {
                try (var resultSet = statement.executeQuery()) {
                    assertFalse(resultSet.next());
                }
            }
        });
    }
}