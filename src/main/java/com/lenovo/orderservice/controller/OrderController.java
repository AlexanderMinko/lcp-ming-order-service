package com.lenovo.orderservice.controller;

import com.lenovo.orderservice.dto.OrderItemResponseDto;
import com.lenovo.orderservice.dto.OrderRequestDto;
import com.lenovo.orderservice.dto.OrderResponseDto;
import com.lenovo.orderservice.entity.Order;
import com.lenovo.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("orders")
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/list")
    public ResponseEntity<List<Order>> getOrders() {
        return new ResponseEntity<>(orderService.list(), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<OrderResponseDto>> getOrders(@RequestParam String email) {
        return new ResponseEntity<>(orderService.getOrdersByEmail(email), HttpStatus.OK);
    }

    @PostMapping(value = "/make", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> makeOrder(@RequestBody OrderRequestDto orderRequestDto) {
        return new ResponseEntity<>(orderService.makeOrder(orderRequestDto), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<List<OrderItemResponseDto>> getOrdersByOrderId(@PathVariable String id) {
        return new ResponseEntity<>(orderService.getOrderItemsByOrderId(id), HttpStatus.OK);
    }

}
