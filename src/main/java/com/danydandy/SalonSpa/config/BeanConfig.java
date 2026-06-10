package com.danydandy.SalonSpa.config;

import com.danydandy.SalonSpa.application.service.*;
import com.danydandy.SalonSpa.domain.ports.in.*;
import com.danydandy.SalonSpa.domain.ports.out.*;
import com.danydandy.SalonSpa.infrastructure.adapter.out.mapper.*;
import com.danydandy.SalonSpa.infrastructure.adapter.out.repository.*;
import com.danydandy.SalonSpa.infrastructure.security.JwtService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class BeanConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthUseCase authUseCase(
            UserRepositoryPort userRepositoryPort,
            SalonRepositoryPort salonRepositoryPort,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        return new AuthServiceImpl(userRepositoryPort, salonRepositoryPort, passwordEncoder, jwtService);
    }

    @Bean
    public SalonUseCase salonUseCase(SalonRepositoryPort salonRepositoryPort, BranchRepositoryPort branchRepositoryPort) {
        return new SalonServiceImpl(salonRepositoryPort, branchRepositoryPort);
    }

    @Bean
    public SalonRepositoryPort salonRepositoryPort(SalonRepository repository, SalonMapper salonMapper) {
        return new SalonRepositoryAdapter(repository, salonMapper);
    }

    @Bean
    public UserUseCase userUseCase(UserRepositoryPort userRepositoryPort, SalonRepositoryPort salonRepositoryPort) {
        return new UserService(userRepositoryPort, salonRepositoryPort);
    }

    @Bean
    public UserRepositoryPort userRepositoryPort(UserRepository repository, UserMapper userMapper) {
        return new UserRepositoryAdapter(repository, userMapper);
    }

    @Bean
    public BranchUseCase branchUseCase(BranchRepositoryPort branchRepositoryPort, SalonRepositoryPort salonRepositoryPort) {
        return new BranchServiceImpl(branchRepositoryPort, salonRepositoryPort);
    }

    @Bean
    public BranchRepositoryPort branchRepositoryPort(BranchRepository repository, BranchMapper branchMapper) {
        return new BranchRepositoryAdapter(repository, branchMapper);
    }

    @Bean
    public ClientUseCase clientUseCase(ClientRepositoryPort clientRepositoryPort, ClinicalRecordRepositoryPort clinicalRecordRepositoryPort, UserRepositoryPort userRepositoryPort, BranchRepositoryPort branchRepositoryPort, ClinicalRecordServiceRepositoryPort clinicalRecordServiceRepositoryPort, ServiceRepositoryPort serviceRepositoryPort) {
        return new ClientServiceImpl(clientRepositoryPort, clinicalRecordRepositoryPort, userRepositoryPort, branchRepositoryPort, clinicalRecordServiceRepositoryPort, serviceRepositoryPort);
    }

    @Bean
    public ClientRepositoryPort clientRepositoryPort(ClientRepository repository, ClientMapper clientMapper) {
        return new ClientRepositoryAdapter(repository, clientMapper);
    }

    @Bean
    public ClinicalRecordUseCase clinicalRecordUseCase(
            ClinicalRecordRepositoryPort clinicalRecordRepositoryPort,
            ClinicalRecordServiceRepositoryPort clinicalRecordServiceRepositoryPort,
            ClientRepositoryPort clientRepositoryPort
    ) {
        return new ClinicalRecordServiceImpl(clinicalRecordRepositoryPort, clinicalRecordServiceRepositoryPort, clientRepositoryPort);
    }

    @Bean
    public ClinicalRecordRepositoryPort clinicalRecordRepositoryPort(ClinicalRecordRepository repository, ClinicalRecordMapper clinicalRecordMapper) {
        return new ClinicalRecordRepositoryAdapter(repository, clinicalRecordMapper);
    }

    @Bean
    public ServiceCategoryUseCase serviceCategoryUseCase(ServiceCategoryRepositoryPort serviceCategoryRepositoryPort, ServiceRepositoryPort serviceRepositoryPort) {
        return new ServiceCategoryServiceImpl(serviceCategoryRepositoryPort, serviceRepositoryPort);
    }

    @Bean
    public ServiceCategoryRepositoryPort serviceCategoryRepositoryPort(ServiceCategoryRepository repository, ServiceCategoryMapper serviceCategoryMapper) {
        return new ServiceCategoryRepositoryAdapter(repository, serviceCategoryMapper);
    }

    @Bean
    public ServiceUseCase serviceUseCase(ServiceRepositoryPort serviceRepositoryPort) {
        return new ServiceServiceImpl(serviceRepositoryPort);
    }

    @Bean
    public ServiceRepositoryPort serviceRepositoryPort(ServiceRepository repository, ServiceMapper serviceMapper) {
        return new ServiceRepositoryAdapter(repository, serviceMapper);
    }

    @Bean
    public ClinicalRecordServiceRepositoryPort clinicalRecordServiceRepositoryPort(ClinicalRecordServiceRepository repository, ClinicalRecordServiceMapper clinicalRecordServiceMapper) {
        return new ClinicalRecordServiceRepositoryAdapter(repository, clinicalRecordServiceMapper);
    }
}
