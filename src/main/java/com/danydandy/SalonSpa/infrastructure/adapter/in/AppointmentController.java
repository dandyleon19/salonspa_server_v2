package com.danydandy.SalonSpa.infrastructure.adapter.in;

import com.danydandy.SalonSpa.application.dto.request.CreateAppointmentRequest;
import com.danydandy.SalonSpa.application.dto.request.UpdateAppointmentRequest;
import com.danydandy.SalonSpa.application.dto.response.AppointmentCalendarResponse;
import com.danydandy.SalonSpa.application.dto.response.AppointmentResponse;
import com.danydandy.SalonSpa.application.dto.response.PageResponse;
import com.danydandy.SalonSpa.application.mapper.RequestDtoMapper;
import com.danydandy.SalonSpa.domain.model.AppointmentStatus;
import com.danydandy.SalonSpa.domain.ports.in.AppointmentUseCase;
import com.danydandy.SalonSpa.infrastructure.adapter.out.mapper.AppointmentMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
@Validated
public class AppointmentController {

    private final AppointmentUseCase appointmentUseCase;
    private final RequestDtoMapper requestDtoMapper;
    private final AppointmentMapper appointmentMapper;

    @PostMapping
    public Mono<ResponseEntity<AppointmentResponse>> create(@Valid @RequestBody CreateAppointmentRequest request) {
        return appointmentUseCase.create(requestDtoMapper.toAppointment(request))
                .map(appointmentMapper::toResponse)
                .map(appointment -> ResponseEntity.status(HttpStatus.CREATED).body(appointment));
    }

    @GetMapping
    public Mono<ResponseEntity<PageResponse<AppointmentResponse>>> getAll(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Positive @Max(100) int size,
            @RequestParam(required = false) @Positive Long branchId,
            @RequestParam(required = false) @Positive Long userId,
            @RequestParam(required = false) @Positive Long clientId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) AppointmentStatus status
    ) {
        return appointmentUseCase.findPage(page, size, branchId, userId, clientId, date, status)
                .map(pageResponse -> PageResponse.of(
                        pageResponse.content().stream().map(appointmentMapper::toResponse).toList(),
                        pageResponse.page(),
                        pageResponse.size(),
                        pageResponse.totalElements()
                ))
                .map(ResponseEntity::ok);
    }

    @GetMapping("/calendar")
    public Mono<ResponseEntity<AppointmentCalendarResponse>> getCalendar(
            @RequestParam @Min(2000) @Max(2100) int year,
            @RequestParam @Min(1) @Max(12) int month,
            @RequestParam(required = false) @Positive Long branchId,
            @RequestParam(required = false) @Positive Long userId,
            @RequestParam(required = false) AppointmentStatus status
    ) {
        YearMonth yearMonth = YearMonth.of(year, month);
        return appointmentUseCase.findByMonth(year, month, branchId, userId, status)
                .map(appointments -> {
                    List<AppointmentResponse> content = appointments.stream()
                            .map(appointmentMapper::toResponse)
                            .toList();
                    return new AppointmentCalendarResponse(
                            year,
                            month,
                            yearMonth.atDay(1),
                            yearMonth.atEndOfMonth(),
                            content.size(),
                            content
                    );
                })
                .map(ResponseEntity::ok);
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<AppointmentResponse>> getById(@PathVariable @Positive Long id) {
        return appointmentUseCase.findById(id)
                .map(appointmentMapper::toResponse)
                .map(ResponseEntity::ok);
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<AppointmentResponse>> update(
            @PathVariable @Positive Long id,
            @Valid @RequestBody UpdateAppointmentRequest request
    ) {
        return appointmentUseCase.update(id, requestDtoMapper.toAppointment(request))
                .map(appointmentMapper::toResponse)
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable @Positive Long id) {
        return appointmentUseCase.delete(id)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }
}
