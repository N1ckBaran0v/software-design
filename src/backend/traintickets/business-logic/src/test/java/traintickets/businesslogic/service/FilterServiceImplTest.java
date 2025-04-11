package traintickets.businesslogic.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import traintickets.businesslogic.exception.EntityNotFoundException;
import traintickets.businesslogic.exception.InvalidEntityException;
import traintickets.businesslogic.model.Filter;
import traintickets.businesslogic.model.UserId;
import traintickets.businesslogic.repository.FilterRepository;
import traintickets.businesslogic.session.SessionManager;
import traintickets.businesslogic.transport.UserInfo;

import java.sql.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class FilterServiceImplTest {
//    @Mock
//    private FilterRepository filterRepository;
//
//    @Mock
//    private SessionManager sessionManager;
//
//    @InjectMocks
//    private FilterServiceImpl filterService;
//
//    @Test
//    void addFilter_positive_saved() {
//        var filter = new Filter(new UserId(1), "filter", "first", "second", 0, Map.of("adult", 2, "child", 1),
//                Date.valueOf("2025-03-19"), Date.valueOf("2025-10-11"));
//        var userInfo = new UserInfo(new UserId(1L), "user_role");
//        var sessionId = UUID.randomUUID();
//        given(sessionManager.getUserInfo(sessionId)).willReturn(userInfo);
//        filterService.addFilter(sessionId, filter);
//        verify(filterRepository).addFilter(userInfo.role(), filter);
//    }
//
//    @Test
//    void addFilter_negative_invalid() {
//        var filter = new Filter(new UserId(1), "filter", "first", "second", 0, Map.of(),
//                Date.valueOf("2025-03-19"), Date.valueOf("2025-10-11"));
//        assertThrows(InvalidEntityException.class, () -> filterService.addFilter(UUID.randomUUID(), filter));
//        verify(filterRepository, never()).addFilter(any(), any());
//        verify(sessionManager, never()).getUserInfo(any());
//    }
//
//    @Test
//    void addFilter_negative_userIdMismatched() {
//        var filter = new Filter(new UserId(2), "filter", "first", "second", 0, Map.of("adult", 2, "child", 1),
//                Date.valueOf("2025-03-19"), Date.valueOf("2025-10-11"));
//        var userInfo = new UserInfo(new UserId(1L), "user_role");
//        var sessionId = UUID.randomUUID();
//        given(sessionManager.getUserInfo(sessionId)).willReturn(userInfo);
//        assertThrows(InvalidEntityException.class, () -> filterService.addFilter(sessionId, filter));
//        verify(filterRepository, never()).addFilter(any(), any());
//    }
//
//    @Test
//    void getFilter_positive_found() {
//        var userId = new UserId(1);
//        var name = "filter";
//        var filter = new Filter(userId, name, "first", "second", 0, Map.of("adult", 2, "child", 1),
//                Date.valueOf("2025-03-19"), Date.valueOf("2025-10-11"));
//        var userInfo = new UserInfo(new UserId(1L), "user_role");
//        var sessionId = UUID.randomUUID();
//        given(sessionManager.getUserInfo(sessionId)).willReturn(userInfo);
//        given(filterRepository.getFilter(userInfo.role(), userId, name)).willReturn(Optional.of(filter));
//        var result = filterService.getFilter(sessionId, name);
//        assertSame(filter, result);
//    }
//
//    @Test
//    void getFilter_negative_notFound() {
//        var userId = new UserId(1);
//        var name = "filter";
//        var userInfo = new UserInfo(new UserId(1L), "user_role");
//        var sessionId = UUID.randomUUID();
//        given(sessionManager.getUserInfo(sessionId)).willReturn(userInfo);
//        given(filterRepository.getFilter(userInfo.role(), userId, name)).willReturn(Optional.empty());
//        assertThrows(EntityNotFoundException.class, () -> filterService.getFilter(sessionId, name));
//    }
//
//    @Test
//    void getFilters_positive_got() {
//        var userId = new UserId(1);
//        var filter1 = new Filter(userId, "filter1", "first", "second", 0, Map.of("adult", 1),
//                Date.valueOf("2025-03-19"), Date.valueOf("2025-10-11"));
//        var filter2 = new Filter(userId, "filter2", "first", "second", 0, Map.of("adult", 1, "child", 1),
//                Date.valueOf("2025-03-19"), Date.valueOf("2025-10-11"));
//        var userInfo = new UserInfo(new UserId(1L), "user_role");
//        var sessionId = UUID.randomUUID();
//        given(sessionManager.getUserInfo(sessionId)).willReturn(userInfo);
//        given(filterRepository.getFilters(userInfo.role(), userId)).willReturn(List.of(filter1, filter2));
//        var result = filterService.getFilters(sessionId);
//        assertNotNull(result);
//        assertEquals(2, result.size());
//        assertEquals(filter1, result.get(0));
//        assertEquals(filter2, result.get(1));
//    }
//
//    @Test
//    void getFilters_positive_empty() {
//        var userId = new UserId(1);
//        var userInfo = new UserInfo(new UserId(1L), "user_role");
//        var sessionId = UUID.randomUUID();
//        given(sessionManager.getUserInfo(sessionId)).willReturn(userInfo);
//        given(filterRepository.getFilters(userInfo.role(), userId)).willReturn(List.of());
//        var result = filterService.getFilters(sessionId);
//        assertNotNull(result);
//        assertTrue(result.isEmpty());
//    }
//
//    @Test
//    void deleteFilter_positive_deleted() {
//        var userId = new UserId(1);
//        var name = "filter";
//        var userInfo = new UserInfo(new UserId(1L), "user_role");
//        var sessionId = UUID.randomUUID();
//        given(sessionManager.getUserInfo(sessionId)).willReturn(userInfo);
//        filterService.deleteFilter(sessionId, name);
//        verify(filterRepository).deleteFilter(userInfo.role(), userId, name);
//    }
}