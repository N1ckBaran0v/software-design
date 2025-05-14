package traintickets.businesslogic.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import traintickets.businesslogic.model.Place;
import traintickets.businesslogic.model.Railcar;
import traintickets.businesslogic.model.RailcarId;
import traintickets.businesslogic.model.UserId;
import traintickets.businesslogic.repository.RailcarRepository;
import traintickets.businesslogic.transport.UserInfo;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RailcarServiceImplTest {
    @Mock
    private RailcarRepository railcarRepository;

    @InjectMocks
    private RailcarServiceImpl railcarService;

    @Test
    void addRailcar_positive_saved() {
        var railcar = new Railcar(null, "1", "cars", List.of(new Place(null, 1, "", "cars", BigDecimal.valueOf(1000))));
        var userInfo = new UserInfo(new UserId("1"), "carrier_role");
        railcarService.addRailcar(userInfo, railcar);
        verify(railcarRepository).addRailcar(userInfo.role(), railcar);
    }

    @Test
    void getRailcars_positive_got() {
        var type = "restaurant";
        var railcarId1 = new RailcarId("1");
        var railcar1 = new Railcar(railcarId1, "1", type, List.of());
        var railcarId2 = new RailcarId("2");
        var railcar2 = new Railcar(railcarId2, "2", type, List.of());
        var userInfo = new UserInfo(new UserId("1"), "carrier_role");
        given(railcarRepository.getRailcarsByType(userInfo.role(), type)).willReturn(List.of(railcar1, railcar2));
        var result = railcarService.getRailcars(userInfo, type);
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(railcar1, result.get(0));
        assertEquals(railcar2, result.get(1));
    }

    @Test
    void getRailcars_positive_empty() {
        var type = "cars";
        var userInfo = new UserInfo(new UserId("1"), "carrier_role");
        given(railcarRepository.getRailcarsByType(userInfo.role(), type)).willReturn(List.of());
        var result = railcarService.getRailcars(userInfo, type);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}