package com.danydandy.SalonSpa.infrastructure.adapter.out.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "product_categories")
public class ProductCategoryEntity {
    @Id
    private Long id;
    private String name;
    private String description;
    @Column("long_description")
    private String longDescription;

    // Relations
    @Column("salon_id")
    private Long salonId;

    @Column("created_at")
    private LocalDateTime createdAt;
    @Column("updated_at")
    private LocalDateTime updatedAt;

}
