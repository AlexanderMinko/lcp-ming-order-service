package com.lenovo.orderservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Product {

    private String id;
    private String name;
    private String description;
    private String imageUrl;
    private Double price;
    private String categoryId;
    private String producerId;

}
