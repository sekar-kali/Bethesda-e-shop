package com.bethesda.catalog.repository;

import com.bethesda.catalog.model.Product;
import com.bethesda.catalog.model.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategory(ProductCategory category);
}
