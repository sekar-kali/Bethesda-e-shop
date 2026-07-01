package com.bethesda.order.service;

import com.bethesda.order.kafka.OrderEventProducer;
import com.bethesda.order.model.Order;
import com.bethesda.order.model.OrderDtos.CreateOrderRequest;
import com.bethesda.order.model.OrderDtos.OrderResponse;
import com.bethesda.order.model.OrderItem;
import com.bethesda.order.model.OrderStatus;
import com.bethesda.order.repository.OrderRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final StockCache stockCache;
    private final OrderEventProducer orderEventProducer;
    private final SimpMessagingTemplate messagingTemplate;

    public OrderService(OrderRepository orderRepository, StockCache stockCache,
                         OrderEventProducer orderEventProducer, SimpMessagingTemplate messagingTemplate) {
        this.orderRepository = orderRepository;
        this.stockCache = stockCache;
        this.orderEventProducer = orderEventProducer;
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Cree une commande et la valide immediatement contre la vue locale
     * de stock (StockCache), alimentee de facon asynchrone par Kafka.
     * En cas de rupture, la commande est marquee OUT_OF_STOCK plutot que
     * rejetee : le client peut la retenter une fois le stock reapprovisionne.
     */
    @Transactional
    public Order create(CreateOrderRequest request) {
        Order order = new Order(request.customerEmail());
        request.items().forEach(i ->
                order.addItem(new OrderItem(i.productId(), i.productName(), i.quantity(), i.unitPrice())));

        boolean allAvailable = order.getItems().stream()
                .allMatch(i -> stockCache.isAvailable(i.getProductId(), i.getQuantity()));

        order.markStatus(allAvailable ? OrderStatus.CONFIRMED : OrderStatus.OUT_OF_STOCK);

        Order saved = orderRepository.save(order);
        orderEventProducer.publish(saved);
        notifyClient(saved);
        return saved;
    }

    @Transactional
    public Order markPaid(Long orderId) {
        Order order = findById(orderId);
        order.markStatus(OrderStatus.PAID);
        Order saved = orderRepository.save(order);
        orderEventProducer.publish(saved);
        notifyClient(saved);
        return saved;
    }

    public Order findById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Commande introuvable : " + id));
    }

    private void notifyClient(Order order) {
        messagingTemplate.convertAndSend("/topic/orders/" + order.getId(), OrderResponse.from(order));
    }
}
