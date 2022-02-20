package com.lenovo.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderItemResponseDto {

    private String productId;
    private String name;
    private String imageUrl;
    private String description;
    private Double price;
    private Integer count;

}
