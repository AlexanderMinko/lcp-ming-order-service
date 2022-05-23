package com.lenovo.orderservice.controller;

import java.util.List;

import com.lenovo.orderservice.dto.OrderItemResponseDto;
import com.lenovo.orderservice.dto.OrderRequestDto;
import com.lenovo.orderservice.dto.OrderResponseDto;
import com.lenovo.orderservice.entity.Order;
import com.lenovo.orderservice.service.OrderService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("orders")
public class OrderController {

  private final OrderService orderService;

  @GetMapping("/list")
  @Operation(summary = "Get list of orders", tags = {"Orders"}, operationId = "getOrdersUsingGET")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Ok"),
      @ApiResponse(responseCode = "400", description = "Bad Request"),
      @ApiResponse(responseCode = "401", description = "Unauthorized"),
      @ApiResponse(responseCode = "403", description = "Forbidden"),
      @ApiResponse(responseCode = "404", description = "Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")
  })
  public ResponseEntity<List<Order>> getOrders() {
    return new ResponseEntity<>(orderService.list(), HttpStatus.OK);
  }

  @GetMapping
  @Operation(summary = "Get order by email", tags = {"Orders"}, operationId = "getOrdersUsingGET")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Ok"),
      @ApiResponse(responseCode = "400", description = "Bad Request"),
      @ApiResponse(responseCode = "401", description = "Unauthorized"),
      @ApiResponse(responseCode = "403", description = "Forbidden"),
      @ApiResponse(responseCode = "404", description = "Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")
  })
  public ResponseEntity<List<OrderResponseDto>> getOrders(@RequestParam String email) {
    return new ResponseEntity<>(orderService.getOrdersByEmail(email), HttpStatus.OK);
  }

  @PostMapping(value = "/make", produces = MediaType.TEXT_PLAIN_VALUE)
  @Operation(summary = "Make order", tags = {"Orders"}, operationId = "makeOrderUsingPOST")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Ok"),
      @ApiResponse(responseCode = "400", description = "Bad Request"),
      @ApiResponse(responseCode = "401", description = "Unauthorized"),
      @ApiResponse(responseCode = "403", description = "Forbidden"),
      @ApiResponse(responseCode = "404", description = "Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")
  })
  public ResponseEntity<String> makeOrder(@RequestBody OrderRequestDto orderRequestDto) {
    return new ResponseEntity<>(orderService.makeOrder(orderRequestDto), HttpStatus.CREATED);
  }

  @GetMapping("/{id}/items")
  @Operation(summary = "Get list of orders", tags = {"Orders"}, operationId = "getOrdersUsingGET")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Ok"),
      @ApiResponse(responseCode = "400", description = "Bad Request"),
      @ApiResponse(responseCode = "401", description = "Unauthorized"),
      @ApiResponse(responseCode = "403", description = "Forbidden"),
      @ApiResponse(responseCode = "404", description = "Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")
  })
  public ResponseEntity<List<OrderItemResponseDto>> getOrderItemsByOrderId(@PathVariable String id) {
    return new ResponseEntity<>(orderService.getOrderItemsByOrderId(id), HttpStatus.OK);
  }
}
