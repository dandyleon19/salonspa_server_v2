package com.danydandy.SalonSpa.infrastructure.adapter.in;

import com.danydandy.SalonSpa.application.dto.response.PublicServiceCategoryResponse;
import com.danydandy.SalonSpa.domain.ports.in.PublicCatalogUseCase;
import com.danydandy.SalonSpa.infrastructure.adapter.out.mapper.PublicCatalogMapper;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
@Validated
public class PublicController {

    private final PublicCatalogUseCase publicCatalogUseCase;
    private final PublicCatalogMapper publicCatalogMapper;

    @GetMapping("/service-categories")
    public Mono<ResponseEntity<List<PublicServiceCategoryResponse>>> getServiceCategories(
            @RequestParam @Positive Long salonId
    ) {
        return publicCatalogUseCase.findCategoriesWithServices(salonId)
                .map(categories -> categories.stream().map(publicCatalogMapper::toResponse).toList())
                .map(ResponseEntity::ok);
    }
}
