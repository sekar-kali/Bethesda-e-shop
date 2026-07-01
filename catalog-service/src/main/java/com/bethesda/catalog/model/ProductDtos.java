package com.bethesda.catalog.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.Instant;

public class ProductDtos {

    public record CreateProductRequest(
            @NotBlank String name,
            String description,
            @NotNull ProductCategory category,
            @NotBlank String origin,
            @NotNull @PositiveOrZero BigDecimal price,
            @NotNull @PositiveOrZero Integer stockQuantity
    ) {}

    public record UpdateStockRequest(
            @NotNull Integer delta
    ) {}

    public record ProductResponse(
            Long id,
            String name,
            String description,
            ProductCategory category,
            String origin,
            BigDecimal price,
            Integer stockQuantity,
            Instant updatedAt
    ) {
        public static ProductResponse from(Product p) {
            return new ProductResponse(p.getId(), p.getName(), p.getDescription(), p.getCategory(),
                    p.getOrigin(), p.getPrice(), p.getStockQuantity(), p.getUpdatedAt());
        }
    }
}
