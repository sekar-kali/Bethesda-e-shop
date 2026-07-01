package com.bethesda.catalog.controller;

import com.bethesda.catalog.model.Product;
import com.bethesda.catalog.model.ProductCategory;
import com.bethesda.catalog.model.ProductDtos.CreateProductRequest;
import com.bethesda.catalog.model.ProductDtos.ProductResponse;
import com.bethesda.catalog.model.ProductDtos.UpdateStockRequest;
import com.bethesda.catalog.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = {"http://localhost:4200"})
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<ProductResponse> list(@RequestParam(required = false) ProductCategory category) {
        List<Product> products = category != null
                ? productService.findByCategory(category)
                : productService.findAll();
        return products.stream().map(ProductResponse::from).toList();
    }

    @GetMapping("/{id}")
    public ProductResponse get(@PathVariable Long id) {
        return ProductResponse.from(productService.findById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponse create(@Valid @RequestBody CreateProductRequest request) {
        return ProductResponse.from(productService.create(request));
    }

    @PatchMapping("/{id}/stock")
    public ProductResponse adjustStock(@PathVariable Long id, @Valid @RequestBody UpdateStockRequest request) {
        return ProductResponse.from(productService.adjustStock(id, request.delta()));
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("catalog-service OK");
    }
}
