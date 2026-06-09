package com.danydandy.SalonSpa.application.mapper;

import com.danydandy.SalonSpa.application.dto.request.*;
import com.danydandy.SalonSpa.domain.model.*;
import org.springframework.stereotype.Component;

@Component
public class RequestDtoMapper {

    public User toUser(CreateUserRequest request) {
        return User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(request.getPassword())
                .role(request.getRole())
                .commissionPercentage(request.getCommissionPercentage())
                .build();
    }

    public User toUser(UpdateUserRequest request) {
        return User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .isActive(request.getIsActive())
                .role(request.getRole())
                .commissionPercentage(request.getCommissionPercentage())
                .build();
    }

    public Salon toSalon(CreateSalonRequest request) {
        return Salon.builder()
                .name(request.getName())
                .socialReason(request.getSocialReason())
                .fiscalAddress(request.getFiscalAddress())
                .rucNumber(request.getRucNumber())
                .phone(request.getPhone())
                .build();
    }

    public Salon toSalon(UpdateSalonRequest request) {
        return Salon.builder()
                .name(request.getName())
                .socialReason(request.getSocialReason())
                .fiscalAddress(request.getFiscalAddress())
                .rucNumber(request.getRucNumber())
                .phone(request.getPhone())
                .build();
    }

    public Client toClient(CreateClientRequest request) {
        return Client.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .documentNumber(request.getDocumentNumber())
                .phone(request.getPhone())
                .email(request.getEmail())
                .birthDate(request.getBirthDate())
                .gender(request.getGender())
                .build();
    }

    public Client toClient(UpdateClientRequest request) {
        return Client.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .documentNumber(request.getDocumentNumber())
                .phone(request.getPhone())
                .email(request.getEmail())
                .birthDate(request.getBirthDate())
                .gender(request.getGender())
                .build();
    }

    public Branch toBranch(CreateBranchRequest request) {
        return Branch.builder()
                .name(request.getName())
                .address(request.getAddress())
                .city(request.getCity())
                .build();
    }

    public Branch toBranch(UpdateBranchRequest request) {
        return Branch.builder()
                .name(request.getName())
                .address(request.getAddress())
                .city(request.getCity())
                .build();
    }

    public Service toService(CreateServiceRequest request) {
        return Service.builder()
                .categoryId(request.getCategoryId())
                .name(request.getName())
                .description(request.getDescription())
                .longDescription(request.getLongDescription())
                .price(request.getPrice())
                .isActive(request.getIsActive())
                .build();
    }

    public Service toService(UpdateServiceRequest request) {
        return Service.builder()
                .name(request.getName())
                .description(request.getDescription())
                .longDescription(request.getLongDescription())
                .price(request.getPrice())
                .isActive(request.getIsActive())
                .build();
    }

    public ServiceCategory toServiceCategory(CreateServiceCategoryRequest request) {
        return ServiceCategory.builder()
                .name(request.getName())
                .description(request.getDescription())
                .longDescription(request.getLongDescription())
                .build();
    }

    public ServiceCategory toServiceCategory(UpdateServiceCategoryRequest request) {
        return ServiceCategory.builder()
                .name(request.getName())
                .description(request.getDescription())
                .longDescription(request.getLongDescription())
                .build();
    }

    public ClinicalRecord toClinicalRecord(CreateClinicalRecordRequest request) {
        return ClinicalRecord.builder()
                .clientId(request.getClientId())
                .userId(request.getUserId())
                .branchId(request.getBranchId())
                .serviceId(request.getServiceId())
                .diagnosis(request.getDiagnosis())
                .treatment(request.getTreatment())
                .observations(request.getObservations())
                .sessionDate(request.getSessionDate())
                .build();
    }

    public ClinicalRecord toClinicalRecord(UpdateClinicalRecordRequest request) {
        return ClinicalRecord.builder()
                .diagnosis(request.getDiagnosis())
                .treatment(request.getTreatment())
                .observations(request.getObservations())
                .sessionDate(request.getSessionDate())
                .build();
    }
}
