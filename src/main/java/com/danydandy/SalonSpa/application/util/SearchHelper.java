package com.danydandy.SalonSpa.application.util;

public final class SearchHelper {

    private SearchHelper() {
    }

    public static String toLikePattern(String search) {
        if (search == null || search.isBlank()) {
            return null;
        }
        return "%" + search.trim() + "%";
    }
}
