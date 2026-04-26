package com.danydandy.SalonSpa.domain.model;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Client {
    private Long id;
    private String firstName;
    private String lastName;
    private String documentNumber;
    private String phone;
    private String email;
    private LocalDate birthDate;
    private Gender gender;
    private Long salonId;
    private List<ClinicalRecord> clinicalRecords;
}
