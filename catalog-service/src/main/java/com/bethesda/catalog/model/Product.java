package com.bethesda.catalog.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductCategory category;

    @NotBlank
    @Column(nullable = false)
    private String origin; // ex: "Kerala, Inde"

    @PositiveOrZero
    @Column(nullable = false)
    private BigDecimal price;

    @PositiveOrZero
    @Column(nullable = false)
    private Integer stockQuantity;

    @Column(nullable = false)
    private Instant updatedAt = Instant.now();

    protected Product() {
        // requis par JPA
    }

    public Product(String name, String description, ProductCategory category,
                    String origin, BigDecimal price, Integer stockQuantity) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.origin = origin;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.updatedAt = Instant.now();
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public ProductCategory getCategory() { return category; }
    public String getOrigin() { return origin; }
    public BigDecimal getPrice() { return price; }
    public Integer getStockQuantity() { return stockQuantity; }
    public Instant getUpdatedAt() { return updatedAt; }

    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setCategory(ProductCategory category) { this.category = category; }
    public void setOrigin(String origin) { this.origin = origin; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public void adjustStock(int delta) {
        this.stockQuantity = Math.max(0, this.stockQuantity + delta);
        this.updatedAt = Instant.now();
    }

    public void touch() {
        this.updatedAt = Instant.now();
    }
}
