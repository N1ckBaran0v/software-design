package traintickets.dataaccess.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import traintickets.businesslogic.exception.EntityAlreadyExistsException;
import traintickets.businesslogic.model.Place;
import traintickets.businesslogic.model.Railcar;
import traintickets.businesslogic.model.RailcarId;
import traintickets.businesslogic.model.TrainId;
import traintickets.businesslogic.repository.RailcarRepository;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RailcarRepositoryImplIT extends PostgresIT {
    private RailcarRepository railcarRepository;

    @BeforeEach
    @Override
    void setUp() {
        super.setUp();
        railcarRepository = new RailcarRepositoryImpl(jdbcTemplate, roleName);
    }

    @Override
    protected void insertData() {
        jdbcTemplate.executeCons(roleName, Connection.TRANSACTION_READ_UNCOMMITTED, conn -> {
            try (var statement = conn.prepareStatement(
                    "insert into trains (train_class) values ('Скорый'); " +
                            "insert into railcars (railcar_model, railcar_type) values " +
                            "('1', 'сидячий'), ('2', 'купе'); " +
                            "insert into railcarsintrains (train_id, railcar_number, railcar_id) values " +
                            "(1, 1, 1), (1, 2, 1), (1, 3, 1), (1, 4, 2); " +
                            "insert into places (railcar_id, place_number, description, purpose, place_cost) values " +
                            "(1, 1, '', 'universal', 100), (1, 2, '', 'child', 50), (2, 1, '', 'universal', 100);"
            )) {
                statement.execute();
            }
        });
    }

    @Test
    void addRailcar_positive_added() {
        var place = new Place(1, "", "universal", BigDecimal.valueOf(100));
        var railcar = new Railcar(null, "3", "купе", List.of(place));
        railcarRepository.addRailcar(railcar);
        jdbcTemplate.executeCons(roleName, Connection.TRANSACTION_READ_UNCOMMITTED, conn -> {
            try (var statement = conn.prepareStatement(
                    "SELECT * FROM railcars WHERE id = 3;"
            )) {
                try (var resultSet = statement.executeQuery()) {
                    assertTrue(resultSet.next());
                    assertEquals(railcar.model(), resultSet.getString(2));
                    assertEquals(railcar.type(), resultSet.getString(3));
                    assertFalse(resultSet.next());
                }
            }
            try (var statement = conn.prepareStatement(
                    "SELECT * FROM places WHERE railcar_id = 3;"
            )) {
                try (var resultSet = statement.executeQuery()) {
                    assertTrue(resultSet.next());
                    assertEquals(place.number(), resultSet.getInt(3));
                    assertEquals(place.description(), resultSet.getString(4));
                    assertEquals(place.purpose(), resultSet.getString(5));
                    assertEquals(place.cost(), resultSet.getBigDecimal(6));
                    assertFalse(resultSet.next());
                }
            }
        });
    }

    @Test
    void addRailcar_negative_exists() {
        assertThrows(EntityAlreadyExistsException.class,
                () -> railcarRepository.addRailcar(new Railcar(null, "2", "купе", List.of())));
    }

    @Test
    void getRailcarsByType_positive_got() {
        var railcar = new Railcar(new RailcarId(2), "2", "купе",
                List.of(new Place(1, "", "universal", BigDecimal.valueOf(100))));
        var result = railcarRepository.getRailcarsByType(railcar.type());
        assertNotNull(result);
        var iterator = result.iterator();
        assertTrue(iterator.hasNext());
        assertEquals(railcar, iterator.next());
        assertFalse(iterator.hasNext());
    }

    @Test
    void getRailcarsByType_positive_empty() {
        var result = railcarRepository.getRailcarsByType("unexpexted");
        assertNotNull(result);
        assertFalse(result.iterator().hasNext());
    }

    @Test
    void getRailcarsByTrain_positive_got() {
        var place1 = new Place(1, "", "universal", BigDecimal.valueOf(100));
        var place2 = new Place(2, "", "child", BigDecimal.valueOf(50));
        var railcar1 = new Railcar(new RailcarId(1), "1", "сидячий", List.of(place1, place2));
        var railcar2 = new Railcar(new RailcarId(2), "2", "купе", List.of(place1));
        var result = railcarRepository.getRailcarsByTrain(new TrainId(1));
        assertNotNull(result);
        var iterator = result.iterator();
        assertTrue(iterator.hasNext());
        assertEquals(railcar1, iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals(railcar2, iterator.next());
        assertFalse(iterator.hasNext());
    }

    @Test
    void getRailcarsByTrain_positive_empty() {
        var result = railcarRepository.getRailcarsByTrain(new TrainId(2));
        assertNotNull(result);
        assertFalse(result.iterator().hasNext());
    }
}