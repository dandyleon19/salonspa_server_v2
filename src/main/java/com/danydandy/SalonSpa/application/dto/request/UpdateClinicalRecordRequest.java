package com.danydandy.SalonSpa.application.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateClinicalRecordRequest {

    private String diagnosis;

    private String treatment;

    private String observations;

    private LocalDateTime sessionDate;
}
