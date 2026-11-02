package com.danydandy.SalonSpa.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Plan {
    private Long id;
    private String code;
    private String name;
    private BigDecimal priceMonthly;
    /** null = unlimited */
    private Integer maxBranches;
    /** null = unlimited */
    private Integer maxUsers;
    private Integer sortOrder;
    private Set<String> features;

    public boolean hasFeature(String featureKey) {
        return features != null && features.contains(featureKey);
    }
}
