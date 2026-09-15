package com.danydandy.SalonSpa.config;

import com.danydandy.SalonSpa.application.service.*;
import com.danydandy.SalonSpa.domain.ports.in.*;
import com.danydandy.SalonSpa.domain.ports.out.*;
import com.danydandy.SalonSpa.infrastructure.adapter.out.mapper.*;
import com.danydandy.SalonSpa.infrastructure.adapter.out.repository.*;
import com.danydandy.SalonSpa.config.properties.JwtProperties;
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
            RefreshTokenRepositoryPort refreshTokenRepositoryPort,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            JwtProperties jwtProperties
    ) {
        return new AuthServiceImpl(
                userRepositoryPort,
                salonRepositoryPort,
                refreshTokenRepositoryPort,
                passwordEncoder,
                jwtService,
                jwtProperties
        );
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
    public RefreshTokenRepositoryPort refreshTokenRepositoryPort(
            RefreshTokenRepository repository,
            RefreshTokenMapper refreshTokenMapper
    ) {
        return new RefreshTokenRepositoryAdapter(repository, refreshTokenMapper);
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
    public ClientUseCase clientUseCase(ClientRepositoryPort clientRepositoryPort, ClinicalRecordRepositoryPort clinicalRecordRepositoryPort, UserRepositoryPort userRepositoryPort, BranchRepositoryPort branchRepositoryPort, ClinicalRecordServiceRepositoryPort clinicalRecordServiceRepositoryPort, ServiceRepositoryPort serviceRepositoryPort, AppointmentUseCase appointmentUseCase) {
        return new ClientServiceImpl(clientRepositoryPort, clinicalRecordRepositoryPort, userRepositoryPort, branchRepositoryPort, clinicalRecordServiceRepositoryPort, serviceRepositoryPort, appointmentUseCase);
    }

    @Bean
    public ClientRepositoryPort clientRepositoryPort(ClientRepository repository, ClientMapper clientMapper) {
        return new ClientRepositoryAdapter(repository, clientMapper);
    }

    @Bean
    public ClinicalRecordUseCase clinicalRecordUseCase(
            ClinicalRecordRepositoryPort clinicalRecordRepositoryPort,
            ClinicalRecordServiceRepositoryPort clinicalRecordServiceRepositoryPort,
            ClientRepositoryPort clientRepositoryPort,
            AppointmentUseCase appointmentUseCase,
            UserRepositoryPort userRepositoryPort,
            BranchRepositoryPort branchRepositoryPort,
            ServiceRepositoryPort serviceRepositoryPort
    ) {
        return new ClinicalRecordServiceImpl(
                clinicalRecordRepositoryPort,
                clinicalRecordServiceRepositoryPort,
                clientRepositoryPort,
                appointmentUseCase,
                userRepositoryPort,
                branchRepositoryPort,
                serviceRepositoryPort
        );
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
    public PublicCatalogUseCase publicCatalogUseCase(
            SalonRepositoryPort salonRepositoryPort,
            ServiceCategoryRepositoryPort serviceCategoryRepositoryPort,
            ServiceRepositoryPort serviceRepositoryPort
    ) {
        return new PublicCatalogServiceImpl(salonRepositoryPort, serviceCategoryRepositoryPort, serviceRepositoryPort);
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

    @Bean
    public AppointmentUseCase appointmentUseCase(
            AppointmentRepositoryPort appointmentRepositoryPort,
            ClientRepositoryPort clientRepositoryPort,
            UserRepositoryPort userRepositoryPort,
            BranchRepositoryPort branchRepositoryPort,
            ServiceRepositoryPort serviceRepositoryPort,
            AppointmentEnricher appointmentEnricher
    ) {
        return new AppointmentServiceImpl(
                appointmentRepositoryPort,
                clientRepositoryPort,
                userRepositoryPort,
                branchRepositoryPort,
                serviceRepositoryPort,
                appointmentEnricher
        );
    }

    @Bean
    public DashboardUseCase dashboardUseCase(
            DashboardRepositoryPort dashboardRepositoryPort,
            AppointmentRepositoryPort appointmentRepositoryPort,
            AppointmentEnricher appointmentEnricher,
            AppointmentMapper appointmentMapper
    ) {
        return new DashboardServiceImpl(dashboardRepositoryPort, appointmentRepositoryPort, appointmentEnricher,
                appointmentMapper);
    }

    @Bean
    public DashboardRepositoryPort dashboardRepositoryPort(DashboardRepository dashboardRepository) {
        return new DashboardRepositoryAdapter(dashboardRepository);
    }

    @Bean
    public AppointmentRepositoryPort appointmentRepositoryPort(AppointmentRepository repository, AppointmentMapper appointmentMapper) {
        return new AppointmentRepositoryAdapter(repository, appointmentMapper);
    }

    @Bean
    public SaleEnricher saleEnricher(
            ClientRepositoryPort clientRepositoryPort,
            BranchRepositoryPort branchRepositoryPort,
            UserRepositoryPort userRepositoryPort,
            SaleItemRepositoryPort saleItemRepositoryPort,
            SalePaymentRepositoryPort salePaymentRepositoryPort
    ) {
        return new SaleEnricher(
                clientRepositoryPort,
                branchRepositoryPort,
                userRepositoryPort,
                saleItemRepositoryPort,
                salePaymentRepositoryPort
        );
    }

    @Bean
    public SaleUseCase saleUseCase(
            SaleRepositoryPort saleRepositoryPort,
            SaleItemRepositoryPort saleItemRepositoryPort,
            SalePaymentRepositoryPort salePaymentRepositoryPort,
            SaleReportRepositoryPort saleReportRepositoryPort,
            ClientRepositoryPort clientRepositoryPort,
            BranchRepositoryPort branchRepositoryPort,
            UserRepositoryPort userRepositoryPort,
            ServiceRepositoryPort serviceRepositoryPort,
            AppointmentRepositoryPort appointmentRepositoryPort,
            SaleEnricher saleEnricher
    ) {
        return new SaleServiceImpl(
                saleRepositoryPort,
                saleItemRepositoryPort,
                salePaymentRepositoryPort,
                saleReportRepositoryPort,
                clientRepositoryPort,
                branchRepositoryPort,
                userRepositoryPort,
                serviceRepositoryPort,
                appointmentRepositoryPort,
                saleEnricher
        );
    }

    @Bean
    public SaleRepositoryPort saleRepositoryPort(SaleRepository repository, SaleMapper saleMapper) {
        return new SaleRepositoryAdapter(repository, saleMapper);
    }

    @Bean
    public SaleItemRepositoryPort saleItemRepositoryPort(SaleItemRepository repository, SaleItemMapper saleItemMapper) {
        return new SaleItemRepositoryAdapter(repository, saleItemMapper);
    }

    @Bean
    public SalePaymentRepositoryPort salePaymentRepositoryPort(
            SalePaymentRepository repository,
            SalePaymentMapper salePaymentMapper
    ) {
        return new SalePaymentRepositoryAdapter(repository, salePaymentMapper);
    }

    @Bean
    public SaleReportRepositoryPort saleReportRepositoryPort(SaleReportRepository saleReportRepository) {
        return new SaleReportRepositoryAdapter(saleReportRepository);
    }

    @Bean
    public UserServiceCommissionUseCase userServiceCommissionUseCase(
            UserServiceCommissionRepositoryPort userServiceCommissionRepositoryPort,
            UserRepositoryPort userRepositoryPort,
            ServiceRepositoryPort serviceRepositoryPort
    ) {
        return new UserServiceCommissionServiceImpl(
                userServiceCommissionRepositoryPort,
                userRepositoryPort,
                serviceRepositoryPort
        );
    }

    @Bean
    public UserServiceCommissionRepositoryPort userServiceCommissionRepositoryPort(
            UserServiceCommissionRepository repository,
            UserServiceCommissionMapper userServiceCommissionMapper
    ) {
        return new UserServiceCommissionRepositoryAdapter(repository, userServiceCommissionMapper);
    }

    @Bean
    public CommissionReportUseCase commissionReportUseCase(
            CommissionReportRepositoryPort commissionReportRepositoryPort
    ) {
        return new CommissionReportServiceImpl(commissionReportRepositoryPort);
    }

    @Bean
    public CommissionReportRepositoryPort commissionReportRepositoryPort(
            CommissionReportRepository commissionReportRepository
    ) {
        return new CommissionReportRepositoryAdapter(commissionReportRepository);
    }
}
