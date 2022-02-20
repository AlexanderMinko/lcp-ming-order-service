package com.lenovo.orderservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

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
    private List<OrderItem> orderItems;
}
