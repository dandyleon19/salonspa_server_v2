package com.danydandy.SalonSpa.application.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateBranchRequest {

    @Size(max = 50, message = "Name must be at most 50 characters")
    private String name;

    @Size(max = 100, message = "Address must be at most 100 characters")
    private String address;

    @Size(max = 100, message = "City must be at most 100 characters")
    private String city;
}
