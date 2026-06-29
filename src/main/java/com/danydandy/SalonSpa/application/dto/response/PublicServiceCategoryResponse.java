package com.danydandy.SalonSpa.application.dto.response;

import java.util.List;

public record PublicServiceCategoryResponse(
        String name,
        String description,
        String longDescription,
        List<PublicServiceResponse> services
) {
}
