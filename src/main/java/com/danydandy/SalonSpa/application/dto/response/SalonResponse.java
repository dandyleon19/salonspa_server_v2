package com.danydandy.SalonSpa.application.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record SalonResponse(
        Long id,
        String name,
        String socialReason,
        String fiscalAddress,
        String rucNumber,
        String phone,
        List<BranchResponse> branches,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
