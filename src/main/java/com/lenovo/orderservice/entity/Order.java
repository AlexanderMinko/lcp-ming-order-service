package com.lenovo.orderservice.entity;

import java.time.Instant;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document
@AllArgsConstructor
@NoArgsConstructor
public class Order {

  @Id
  private String id;
  private Instant createdDate;
  private String email;
  private Account account;
  private List<OrderItem> orderItems;
}
