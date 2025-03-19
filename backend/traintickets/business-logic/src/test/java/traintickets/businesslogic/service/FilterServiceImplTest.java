package traintickets.businesslogic.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import traintickets.businesslogic.exception.EntityNotFoundException;
import traintickets.businesslogic.model.Filter;
import traintickets.businesslogic.model.UserId;
import traintickets.businesslogic.repository.FilterRepository;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class FilterServiceImplTest {
    @Mock
    FilterRepository filterRepository;

    @InjectMocks
    FilterServiceImpl filterService;

    @Test
    void addFilter_positive_saved() {
        var filter = new Filter(new UserId(1), "filter", "first", "second",
                "express", 0, List.of("adult", "adult", "child"),
                Date.valueOf("2025-03-19"), Date.valueOf("2025-10-11"),
                BigDecimal.valueOf(100), BigDecimal.valueOf(10000));
        filterService.addFilter(filter);
        verify(filterRepository).addFilter(filter);
    }

    @Test
    void getFilter_positive_found() {
        var userId = new UserId(1);
        var name = "filter";
        var filter = new Filter(userId, name, "first", "second",
                "express", 0, List.of("adult", "adult", "child"),
                Date.valueOf("2025-03-19"), Date.valueOf("2025-10-11"),
                BigDecimal.valueOf(100), BigDecimal.valueOf(10000));
        given(filterRepository.getFilter(userId, name)).willReturn(Optional.of(filter));
        var result = filterService.getFilter(userId, name);
        assertSame(filter, result);
    }

    @Test
    void getFilter_negative_notFound() {
        var userId = new UserId(1);
        var name = "filter";
        given(filterRepository.getFilter(userId, name)).willReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> filterService.getFilter(userId, name));
    }

    @Test
    void getFilters_positive_got() {
        var userId = new UserId(1);
        var filter1 = new Filter(userId, "filter1", "first", "second",
                "regular", 0, List.of("adult"),
                Date.valueOf("2025-03-19"), Date.valueOf("2025-10-11"),
                BigDecimal.valueOf(100), BigDecimal.valueOf(10000));
        var filter2 = new Filter(userId, "filter2", "first", "second",
                "express", 0, List.of("adult", "child"),
                Date.valueOf("2025-03-19"), Date.valueOf("2025-10-11"),
                BigDecimal.valueOf(100), BigDecimal.valueOf(10000));
        given(filterRepository.getFilters(userId)).willReturn(List.of(filter1, filter2));
        var result = filterService.getFilters(userId);
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(filter1, result.get(0));
        assertEquals(filter2, result.get(1));
    }

    @Test
    void getFilters_positive_empty() {
        var userId = new UserId(1);
        given(filterRepository.getFilters(userId)).willReturn(List.of());
        var result = filterService.getFilters(userId);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void deleteFilter_positive_deleted() {
        var userId = new UserId(1);
        var name = "filter";
        filterService.deleteFilter(userId, name);
        verify(filterRepository).deleteFilter(userId, name);
    }
}