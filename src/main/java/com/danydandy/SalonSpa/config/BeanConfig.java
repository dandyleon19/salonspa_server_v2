package com.danydandy.SalonSpa.config;

import com.danydandy.SalonSpa.application.service.*;
import com.danydandy.SalonSpa.domain.ports.in.*;
import com.danydandy.SalonSpa.domain.ports.out.*;
import com.danydandy.SalonSpa.infrastructure.adapter.out.mapper.ClientMapper;
import com.danydandy.SalonSpa.infrastructure.adapter.out.mapper.ClinicalRecordMapper;
import com.danydandy.SalonSpa.infrastructure.adapter.out.mapper.SalonMapper;
import com.danydandy.SalonSpa.infrastructure.adapter.out.repository.*;
import com.danydandy.SalonSpa.infrastructure.security.JwtService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.reactive.CorsWebFilter;

import java.util.Arrays;
import java.util.List;

@Configuration
public class BeanConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthUseCase authUseCase(UserRepositoryPort userRepositoryPort, PasswordEncoder passwordEncoder, JwtService jwtService) {
        return new AuthService(userRepositoryPort, passwordEncoder, jwtService);
    }

    @Bean
    public SalonUseCase salonUseCase(SalonRepositoryPort salonRepositoryPort, BranchRepositoryPort branchRepositoryPort) {
        return new SalonService(salonRepositoryPort, branchRepositoryPort);
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
    public UserRepositoryPort userRepositoryPort(UserRepository repository) {
        return new UserRepositoryAdapter(repository);
    }

    @Bean
    public BranchUseCase branchUseCase(BranchRepositoryPort branchRepositoryPort) {
        return new BranchService(branchRepositoryPort);
    }

    @Bean
    public BranchRepositoryPort branchRepositoryPort(BranchRepository repository) {
        return new BranchRepositoryAdapter(repository);
    }

    @Bean
    public ClientUseCase clientUseCase(ClientRepositoryPort clientRepositoryPort, ClinicalRecordRepositoryPort clinicalRecordRepositoryPort) {
        return new ClientService(clientRepositoryPort, clinicalRecordRepositoryPort);
    }

    @Bean
    public ClientRepositoryPort clientRepositoryPort(ClientRepository repository, ClientMapper clientMapper) {
        return new ClientRepositoryAdapter(repository, clientMapper);
    }

    @Bean
    public ClinicalRecordUseCase clinicalRecordUseCase(ClinicalRecordRepositoryPort clinicalRecordRepositoryPort) {
        return new ClinicalRecordService(clinicalRecordRepositoryPort);
    }

    @Bean
    public ClinicalRecordRepositoryPort clinicalRecordRepositoryPort(ClinicalRecordRepository repository, ClinicalRecordMapper clinicalRecordMapper) {
        return new ClinicalRecordRepositoryAdapter(repository, clinicalRecordMapper);
    }

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:3000"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsWebFilter(source);
    }
}
