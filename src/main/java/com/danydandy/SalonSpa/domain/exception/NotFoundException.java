package com.danydandy.SalonSpa.domain.exception;

import org.springframework.http.HttpStatus;

import java.util.Map;

public class NotFoundException extends ApiException {

    /** What the user calls each resource (the code uses the entity names). */
    private static final Map<String, String> LABELS = Map.ofEntries(
            Map.entry("Appointment", "la cita"),
            Map.entry("Branch", "la sucursal"),
            Map.entry("Client", "el cliente"),
            Map.entry("ClinicalRecord", "el historial clínico"),
            Map.entry("Product", "el producto"),
            Map.entry("ProductCategory", "la categoría de producto"),
            Map.entry("Sale", "la venta"),
            Map.entry("Salon", "el salón"),
            Map.entry("Service", "el servicio"),
            Map.entry("ServiceCategory", "la categoría de servicio"),
            Map.entry("User", "el usuario")
    );

    public NotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, "NOT_FOUND", message);
    }

    public static NotFoundException forResource(String resource, Object id) {
        return new NotFoundException("No se encontró " + LABELS.getOrDefault(resource, "el registro") + " con id " + id);
    }
}
