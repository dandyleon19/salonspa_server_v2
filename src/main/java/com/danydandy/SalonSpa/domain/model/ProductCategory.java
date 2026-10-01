package com.danydandy.SalonSpa.domain.model;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductCategory {
    private Long id;
    private String name;
    private String description;
    private String longDescription;
    private Long salonId;
    private List<Product> products;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
