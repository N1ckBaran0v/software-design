package traintickets.businesslogic.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import traintickets.businesslogic.exception.EntityNotFoundException;
import traintickets.businesslogic.exception.InvalidEntityException;
import traintickets.businesslogic.model.*;
import traintickets.businesslogic.repository.RailcarRepository;
import traintickets.businesslogic.repository.TrainRepository;
import traintickets.businesslogic.session.SessionManager;
import traintickets.businesslogic.transport.UserInfo;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TrainServiceImplTest {
//    @Mock
//    private TrainRepository trainRepository;
//
//    @Mock
//    private RailcarRepository railcarRepository;
//
//    @Mock
//    private SessionManager sessionManager;
//
//    @InjectMocks
//    private TrainServiceImpl trainService;
//
//    @Test
//    void addTrain_positive_saved() {
//        var train = new Train(null, "express", List.of(new RailcarId(1), new RailcarId(2)));
//        var userInfo = new UserInfo(new UserId(1), "carrier_role");
//        var sessionId = UUID.randomUUID();
//        given(sessionManager.getUserInfo(sessionId)).willReturn(userInfo);
//        trainService.addTrain(sessionId, train);
//        verify(trainRepository).addTrain(userInfo.role(), train);
//    }
//
//    @Test
//    void addTrain_negative_invalid() {
//        var train = new Train(null, "express", List.of());
//        assertThrows(InvalidEntityException.class, () -> trainService.addTrain(UUID.randomUUID(), train));
//        verify(trainRepository, never()).addTrain(any(), any());
//        verify(sessionManager, never()).getUserInfo(any());
//    }
//
//    @Test
//    void getTrain_positive_found() {
//        var trainId = new TrainId(1);
//        var train = new Train(trainId, "express", List.of(new RailcarId(1), new RailcarId(2)));
//        var userInfo = new UserInfo(new UserId(1), "carrier_role");
//        var sessionId = UUID.randomUUID();
//        given(sessionManager.getUserInfo(sessionId)).willReturn(userInfo);
//        given(trainRepository.getTrain(userInfo.role(), trainId)).willReturn(Optional.of(train));
//        var result = trainService.getTrain(sessionId, trainId);
//        assertSame(train, result);
//    }
//
//    @Test
//    void getTrain_negative_notFound() {
//        var trainId = new TrainId(1);
//        var userInfo = new UserInfo(new UserId(1), "carrier_role");
//        var sessionId = UUID.randomUUID();
//        given(sessionManager.getUserInfo(sessionId)).willReturn(userInfo);
//        given(trainRepository.getTrain(userInfo.role(), trainId)).willReturn(Optional.empty());
//        assertThrows(EntityNotFoundException.class, () -> trainService.getTrain(sessionId, trainId));
//    }
//
//    @Test
//    void getTrains_positive_got() {
//        var train1 = new Train(new TrainId(1), "express", List.of(new RailcarId(1)));
//        var train2 = new Train(new TrainId(2), "express", List.of(new RailcarId(1)));
//        var start = Timestamp.valueOf("2025-03-19 10:10:00");
//        var end = Timestamp.valueOf("2025-03-19 11:40:00");
//        var userInfo = new UserInfo(new UserId(1), "carrier_role");
//        var sessionId = UUID.randomUUID();
//        given(sessionManager.getUserInfo(sessionId)).willReturn(userInfo);
//        given(trainRepository.getTrains(userInfo.role(), start, end)).willReturn(List.of(train1, train2));
//        var result = trainService.getTrains(sessionId, start, end);
//        assertNotNull(result);
//        assertEquals(2, result.size());
//        assertEquals(train1, result.get(0));
//        assertEquals(train2, result.get(1));
//    }
//
//    @Test
//    void getTrains_positive_empty() {
//        var start = Timestamp.valueOf("2025-03-19 10:10:00");
//        var end = Timestamp.valueOf("2025-03-19 11:40:00");
//        var userInfo = new UserInfo(new UserId(1), "carrier_role");
//        var sessionId = UUID.randomUUID();
//        given(sessionManager.getUserInfo(sessionId)).willReturn(userInfo);
//        given(trainRepository.getTrains(userInfo.role(), start, end)).willReturn(List.of());
//        var result = trainService.getTrains(sessionId, start, end);
//        assertNotNull(result);
//        assertTrue(result.isEmpty());
//    }
//
//    @Test
//    void addRailcar_positive_saved() {
//        var railcar = new Railcar(null, "1", "cars", List.of(new Place(null, 1, "", "cars", BigDecimal.valueOf(1000))));
//        var userInfo = new UserInfo(new UserId(1), "carrier_role");
//        var sessionId = UUID.randomUUID();
//        given(sessionManager.getUserInfo(sessionId)).willReturn(userInfo);
//        trainService.addRailcar(sessionId, railcar);
//        verify(railcarRepository).addRailcar(userInfo.role(), railcar);
//    }
//
//    @Test
//    void getRailcars_positive_got() {
//        var type = "restaurant";
//        var railcarId1 = new RailcarId(1);
//        var railcar1 = new Railcar(railcarId1, "1", type, List.of());
//        var railcarId2 = new RailcarId(2);
//        var railcar2 = new Railcar(railcarId2, "2", type, List.of());
//        var userInfo = new UserInfo(new UserId(1), "carrier_role");
//        var sessionId = UUID.randomUUID();
//        given(sessionManager.getUserInfo(sessionId)).willReturn(userInfo);
//        given(railcarRepository.getRailcarsByType(userInfo.role(), type)).willReturn(List.of(railcar1, railcar2));
//        var result = trainService.getRailcars(sessionId, type);
//        assertNotNull(result);
//        assertEquals(2, result.size());
//        assertEquals(railcar1, result.get(0));
//        assertEquals(railcar2, result.get(1));
//    }
//
//    @Test
//    void getRailcars_positive_empty() {
//        var type = "cars";
//        var userInfo = new UserInfo(new UserId(1), "carrier_role");
//        var sessionId = UUID.randomUUID();
//        given(sessionManager.getUserInfo(sessionId)).willReturn(userInfo);
//        given(railcarRepository.getRailcarsByType(userInfo.role(), type)).willReturn(List.of());
//        var result = trainService.getRailcars(sessionId, type);
//        assertNotNull(result);
//        assertTrue(result.isEmpty());
//    }
}