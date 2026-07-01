package com.bethesda.catalog.service;

import com.bethesda.catalog.kafka.StockEvent;
import com.bethesda.catalog.kafka.StockEventProducer;
import com.bethesda.catalog.model.Product;
import com.bethesda.catalog.model.ProductCategory;
import com.bethesda.catalog.model.ProductDtos.CreateProductRequest;
import com.bethesda.catalog.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final StockEventProducer stockEventProducer;

    public ProductService(ProductRepository productRepository, StockEventProducer stockEventProducer) {
        this.productRepository = productRepository;
        this.stockEventProducer = stockEventProducer;
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public List<Product> findByCategory(ProductCategory category) {
        return productRepository.findByCategory(category);
    }

    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produit introuvable : " + id));
    }

    @Transactional
    public Product create(CreateProductRequest request) {
        Product product = new Product(
                request.name(),
                request.description(),
                request.category(),
                request.origin(),
                request.price(),
                request.stockQuantity()
        );
        Product saved = productRepository.save(product);
        stockEventProducer.publish(saved, StockEvent.StockEventType.PRODUCT_CREATED);
        return saved;
    }

    /**
     * Ajuste le stock d'un produit (delta positif = reappro, negatif = vente)
     * et publie l'evenement Kafka correspondant. Appele en interne par le
     * catalogue, ou en reaction a un evenement "OrderConfirmed" consomme
     * depuis order-service (cf. StockEventListener dans order-service).
     */
    @Transactional
    public Product adjustStock(Long productId, int delta) {
        Product product = findById(productId);
        product.adjustStock(delta);
        Product saved = productRepository.save(product);
        stockEventProducer.publish(saved, StockEvent.StockEventType.STOCK_UPDATED);
        return saved;
    }
}
