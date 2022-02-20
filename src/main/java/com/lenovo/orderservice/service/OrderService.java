package com.lenovo.orderservice.service;

import com.lenovo.exception.ProductNotFound;
import com.lenovo.model.NotificationEmail;
import com.lenovo.model.events.NotificationEmailEvent;
import com.lenovo.orderservice.config.AppConfig;
import com.lenovo.orderservice.dto.OrderItemRequestDto;
import com.lenovo.orderservice.dto.OrderItemResponseDto;
import com.lenovo.orderservice.dto.OrderRequestDto;
import com.lenovo.orderservice.dto.OrderResponseDto;
import com.lenovo.orderservice.entity.Order;
import com.lenovo.orderservice.entity.OrderItem;
import com.lenovo.orderservice.entity.Product;
import com.lenovo.orderservice.mapper.OrderMapper;
import com.lenovo.orderservice.repository.OrderRepository;
import com.lenovo.service.EventProducerService;
import com.lenovo.service.LcpJwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderService {

    private final OrderMapper orderMapper;
    private final OrderRepository orderRepository;
    private final WebClient webClient;
    private final EventProducerService eventProducerService;
    private final LcpJwtService lcpJwtService;
    private final AppConfig config;

    public String makeOrder(OrderRequestDto orderRequestDto) {
        var productsIds = retrieveProductIds(orderRequestDto);
        var products = retrieveProducts(productsIds);
        var orderItems = convertToOrderItems(orderRequestDto, products);
        var orderId = UUID.randomUUID().toString();
        var order = Order.builder()
            .id(orderId)
            .orderItems(orderItems)
            .email(lcpJwtService.getEmail())
            .createdDate(Instant.now())
            .build();
        orderRepository.save(order);
        sendEmail(order);
        log.debug("order saved with ID: {}", orderId);
        return orderId;
    }

    private List<OrderItem> convertToOrderItems(OrderRequestDto orderRequestDto, List<Product> products) {
        return orderRequestDto.getOrderItemRequestsDto().stream()
                .map(dto -> convertProductsToOrderItems(products, dto))
                .collect(Collectors.toList());
    }

    private List<Product> retrieveProducts(Set<String> productsIds) {
        return webClient
                .post()
                .uri(config.getProductServiceUrl() + "/products/by-ids")
                .bodyValue(productsIds)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Product>>() {})
                .block();
    }

    private Set<String> retrieveProductIds(OrderRequestDto orderRequestDto) {
        return orderRequestDto.getOrderItemRequestsDto().stream()
                .map(OrderItemRequestDto::getProductId)
                .collect(Collectors.toSet());
    }

    private void sendEmail(Order order) {
        var event = new NotificationEmailEvent();
        var notificationEmail = NotificationEmail.builder()
                .body("Your order successfully created with id " + order.getId())
                .recipient(order.getEmail())
                .subject("Order created")
                .build();
        event.setTopicName("send-email-event");
        event.setNotificationEmail(notificationEmail);
        event.setCreatedDate(Instant.now());
        eventProducerService.sendEvent(event);
    }

    @Transactional(readOnly = true)
    public List<OrderResponseDto> getOrdersByEmail(String email) {
        var orderResponseDtos = orderRepository.findByEmail(email)
                .stream()
                .map(orderMapper::mapToDto)
                .collect(Collectors.toList());
        log.debug("{} orderResponseDtos found", orderResponseDtos.size());
        return orderResponseDtos;
    }

    private OrderItem convertProductsToOrderItems(List<Product> products, OrderItemRequestDto orderItemRequestDto) {
        var currentId = orderItemRequestDto.getProductId();
        var product = products.stream()
                .filter(currentProduct -> currentProduct.getId().equals(currentId))
                .findFirst()
                .orElseThrow(() -> new ProductNotFound("product not found with id - " + currentId));
        return OrderItem.builder()
            .productId(product.getId())
            .count(orderItemRequestDto.getCount())
            .build();
    }

    @Transactional
    public List<Order> list() {
        return orderRepository.findAll();
    }

    public List<OrderItemResponseDto> getOrderItemsByOrderId(String id) {
        var order = orderRepository.findById(id)
            .orElse(new Order());
        var orderItems = order.getOrderItems();
        var productsIds = orderItems.stream().map(OrderItem::getProductId).collect(Collectors.toSet());
        var products = retrieveProducts(productsIds);
        return products
            .stream()
            .map(product -> {
                var currentOrderItem = orderItems
                    .stream()
                    .filter(orderItem -> orderItem.getProductId().equals(product.getId()))
                    .findFirst().orElse(new OrderItem());
                var orderItemResponseDto = orderMapper.mapToOrderItemResponseDtoFromProduct(product);
                orderItemResponseDto.setCount(currentOrderItem.getCount());
                return orderItemResponseDto;
            })
            .collect(Collectors.toList());
    }
}
