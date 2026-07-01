package com.bethesda.order.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public class OrderDtos {

    public record OrderItemRequest(
            @NotNull Long productId,
            @NotNull String productName,
            @NotNull @Positive Integer quantity,
            @NotNull BigDecimal unitPrice
    ) {}

    public record CreateOrderRequest(
            @NotNull @Email String customerEmail,
            @NotEmpty List<OrderItemRequest> items
    ) {}

    public record OrderResponse(
            Long id,
            String customerEmail,
            OrderStatus status,
            BigDecimal totalAmount,
            Instant createdAt,
            List<OrderItemResponse> items
    ) {
        public static OrderResponse from(Order o) {
            return new OrderResponse(
                    o.getId(), o.getCustomerEmail(), o.getStatus(), o.getTotalAmount(), o.getCreatedAt(),
                    o.getItems().stream()
                            .map(i -> new OrderItemResponse(i.getProductId(), i.getProductName(), i.getQuantity(), i.getUnitPrice()))
                            .toList()
            );
        }
    }

    public record OrderItemResponse(Long productId, String productName, Integer quantity, BigDecimal unitPrice) {}
}
