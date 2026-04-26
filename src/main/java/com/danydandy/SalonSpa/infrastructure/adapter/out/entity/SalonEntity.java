package com.danydandy.SalonSpa.infrastructure.adapter.out.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "salons")
public class SalonEntity {

    @Id
    private Long id;
    private String name;
    @Column("social_reason")
    private String socialReason;
    @Column("fiscal_address")
    private String fiscalAddress;
    @Column("ruc_number")
    private String rucNumber;
    private String phone;
}
