package com.danydandy.SalonSpa.application.dto.response;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PageResponseTest {

    @Test
    void shouldBuildPageResponseWithNavigationFlags() {
        PageResponse<String> page = PageResponse.of(List.of("a", "b"), 1, 2, 5);

        assertEquals(2, page.content().size());
        assertEquals(1, page.page());
        assertEquals(2, page.size());
        assertEquals(5, page.totalElements());
        assertEquals(3, page.totalPages());
        assertTrue(page.hasNext());
        assertTrue(page.hasPrevious());
    }

    @Test
    void shouldIndicateNoNextPageOnLastPage() {
        PageResponse<String> page = PageResponse.of(List.of("a"), 2, 2, 5);

        assertFalse(page.hasNext());
        assertTrue(page.hasPrevious());
    }
}
