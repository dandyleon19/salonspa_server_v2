package com.danydandy.SalonSpa.infrastructure.adapter.out.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "products")
public class ProductEntity {
    @Id
    private Long id;
    private String name;
    private String description;
    @Column("long_description")
    private String longDescription;
    private BigDecimal price;
    @Column("stock_quantity")
    private Integer stockQuantity;
    @Column("low_stock_threshold")
    private Integer lowStockThreshold;
    @Column("is_active")
    private Boolean isActive;
    @Column("image_url")
    private String imageUrl;

    // Relations
    @Column("category_id")
    private Long categoryId;
    @Column("salon_id")
    private Long salonId;

    @Column("created_at")
    private LocalDateTime createdAt;
    @Column("updated_at")
    private LocalDateTime updatedAt;
}
