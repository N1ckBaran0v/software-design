package ru.traintickets.businesslogic.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.traintickets.businesslogic.exception.EntityNotFoundException;
import ru.traintickets.businesslogic.model.*;
import ru.traintickets.businesslogic.repository.RailcarRepository;
import ru.traintickets.businesslogic.repository.TrainRepository;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TrainServiceImplTest {
    @Mock
    private TrainRepository trainRepository;

    @Mock
    private RailcarRepository railcarRepository;

    @InjectMocks
    private TrainServiceImpl trainService;

    @Test
    void addTrain_positive_saved() {
        var train = new Train(null, "express", List.of(new RailcarId(1), new RailcarId(2)));
        trainService.addTrain(train);
        verify(trainRepository).addTrain(train);
    }

    @Test
    void getTrain_positive_found() {
        var trainId = new TrainId(1);
        var train = new Train(trainId, "express", List.of(new RailcarId(1), new RailcarId(2)));
        given(trainRepository.getTrain(trainId)).willReturn(Optional.of(train));
        var result = trainService.getTrain(trainId);
        assertSame(train, result);
    }

    @Test
    void getTrain_negative_notFound() {
        var trainId = new TrainId(1);
        given(trainRepository.getTrain(trainId)).willReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> trainService.getTrain(trainId));
    }

    @Test
    void getTrains_positive_got() {
        var train1 = new Train(new TrainId(1), "express", List.of(new RailcarId(1)));
        var train2 = new Train(new TrainId(2), "express", List.of(new RailcarId(1)));
        var start = Timestamp.valueOf("2025-03-19 10:10:00");
        var end = Timestamp.valueOf("2025-03-19 11:40:00");
        given(trainRepository.getTrains(start, end)).willReturn(List.of(train1, train2));
        var result = trainService.getTrains(start, end);
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(train1, result.get(0));
        assertEquals(train2, result.get(1));
    }

    @Test
    void getTrains_positive_empty() {
        var start = Timestamp.valueOf("2025-03-19 10:10:00");
        var end = Timestamp.valueOf("2025-03-19 11:40:00");
        given(trainRepository.getTrains(start, end)).willReturn(List.of());
        var result = trainService.getTrains(start, end);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void addRailcar_positive_saved() {
        var railcar = new Railcar(null, "cars", List.of(new Place(1, null, "cars", BigDecimal.valueOf(1000))));
        trainService.addRailcar(railcar);
        verify(railcarRepository).addRailcar(railcar);
    }

    @Test
    void getRailcars_positive_got() {
        var type = "restaurant";
        var railcarId1 = new RailcarId(1);
        var railcar1 = new Railcar(railcarId1, type, List.of());
        var railcarId2 = new RailcarId(2);
        var railcar2 = new Railcar(railcarId2, type, List.of());
        given(railcarRepository.getRailcarsByType(type)).willReturn(List.of(railcar1, railcar2));
        var result = trainService.getRailcars(type);
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(railcar1, result.get(0));
        assertEquals(railcar2, result.get(1));
    }

    @Test
    void getRailcars_positive_empty() {
        var type = "cars";
        given(railcarRepository.getRailcarsByType(type)).willReturn(List.of());
        var result = trainService.getRailcars(type);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}