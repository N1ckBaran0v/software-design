package traintickets.businesslogic.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import traintickets.businesslogic.exception.EntityNotFoundException;
import traintickets.businesslogic.exception.InvalidEntityException;
import traintickets.businesslogic.model.*;
import traintickets.businesslogic.repository.TrainRepository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TrainServiceImplTest {
    @Mock
    private TrainRepository trainRepository;

    @InjectMocks
    private TrainServiceImpl trainService;

    @Test
    void addTrain_positive_saved() {
        var train = new Train(null, "express", List.of(new RailcarId("1"), new RailcarId("2")));
        trainService.addTrain(train);
        verify(trainRepository).addTrain(train);
    }

    @Test
    void addTrain_negative_invalid() {
        var train = new Train(null, "express", List.of());
        assertThrows(InvalidEntityException.class, () -> trainService.addTrain(train));
        verify(trainRepository, never()).addTrain(any());
    }

    @Test
    void getTrain_positive_found() {
        var trainId = new TrainId("1");
        var train = new Train(trainId, "express", List.of(new RailcarId("1"), new RailcarId("2")));
        given(trainRepository.getTrain(trainId)).willReturn(Optional.of(train));
        var result = trainService.getTrain(trainId);
        assertSame(train, result);
    }

    @Test
    void getTrain_negative_notFound() {
        var trainId = new TrainId("1");
        given(trainRepository.getTrain(trainId)).willReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> trainService.getTrain(trainId));
    }

    @Test
    void getTrains_positive_got() {
        var train1 = new Train(new TrainId("1"), "express", List.of(new RailcarId("1")));
        var train2 = new Train(new TrainId("2"), "express", List.of(new RailcarId("1")));
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
}