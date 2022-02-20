package com.lenovo.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderResponseDto {

    private String id;
    private Date createdDate;
    private List<OrderItemResponseDto> orderItemResponsesDto;

}
