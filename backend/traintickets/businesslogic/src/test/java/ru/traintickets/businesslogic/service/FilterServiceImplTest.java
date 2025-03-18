package ru.traintickets.businesslogic.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.traintickets.businesslogic.exception.EntityNotFoundException;
import ru.traintickets.businesslogic.model.Filter;
import ru.traintickets.businesslogic.model.UserId;
import ru.traintickets.businesslogic.repository.FilterRepository;

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
        var filter = new Filter(new UserId("random_username"),
                "filter",
                "express",
                List.of("adult", "adult", "child"),
                Date.valueOf("2025-03-19"),
                Date.valueOf("2025-10-11"),
                BigDecimal.valueOf(100),
                BigDecimal.valueOf(10000));
        filterService.addFilter(filter);
        verify(filterRepository).addFilter(filter);
    }

    @Test
    void getFilter_positive_found() {
        var userId = new UserId("random_username");
        var name = "filter";
        var filter = new Filter(userId,
                name,
                "express",
                List.of("adult", "adult", "child"),
                Date.valueOf("2025-03-19"),
                Date.valueOf("2025-10-11"),
                BigDecimal.valueOf(100),
                BigDecimal.valueOf(10000));
        given(filterRepository.getFilter(userId, name)).willReturn(Optional.of(filter));
        var result = filterService.getFilter(userId, name);
        assertSame(filter, result);
    }

    @Test
    void getFilter_negative_notFound() {
        var userId = new UserId("random_username");
        var name = "filter";
        given(filterRepository.getFilter(userId, name)).willReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> filterService.getFilter(userId, name));
    }

    @Test
    void getFilters_positive_got() {
        var userId = new UserId("random_username");
        var filter1 = new Filter(userId,
                "first",
                "regular",
                List.of("adult"),
                Date.valueOf("2025-03-19"),
                Date.valueOf("2025-10-11"),
                BigDecimal.valueOf(100),
                BigDecimal.valueOf(10000));
        var filter2 = new Filter(userId,
                "second",
                "express",
                List.of("adult", "child"),
                Date.valueOf("2025-03-19"),
                Date.valueOf("2025-10-11"),
                BigDecimal.valueOf(100),
                BigDecimal.valueOf(10000));
        given(filterRepository.getFilters(userId)).willReturn(List.of(filter1, filter2));
        var result = filterService.getFilters(userId);
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(filter1, result.get(0));
        assertEquals(filter2, result.get(1));
    }

    @Test
    void getFilters_positive_empty() {
        var userId = new UserId("random_username");
        given(filterRepository.getFilters(userId)).willReturn(List.of());
        var result = filterService.getFilters(userId);
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    void deleteFilter_positive_deleted() {
        var userId = new UserId("random_username");
        var name = "filter";
        filterService.deleteFilter(userId, name);
        verify(filterRepository).deleteFilter(userId, name);
    }
}