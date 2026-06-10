package com.danydandy.SalonSpa.application.dto.response;

public record BranchResponse(
        Long id,
        String name,
        String address,
        String city,
        Long salonId,
        String salonName
) {
}
