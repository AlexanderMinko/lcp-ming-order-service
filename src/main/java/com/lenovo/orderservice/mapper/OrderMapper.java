package com.lenovo.orderservice.mapper;

import com.lenovo.orderservice.dto.OrderItemResponseDto;
import com.lenovo.orderservice.dto.OrderRequestDto;
import com.lenovo.orderservice.dto.OrderResponseDto;
import com.lenovo.orderservice.entity.Order;
import com.lenovo.orderservice.entity.Product;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper
public abstract class OrderMapper {

  //    @Autowired
//    private ProductService productService;
//
//    @Autowired
//    private AccountService accountService;
//
//    @Mapping(target = "id", ignore = true)
//    @Mapping(target = "count", source = "orderItem.count")
//    @Mapping(target = "product", expression = "java(getProduct(orderItem.getProductId()))")
//    public abstract OrderItem mapFromDto(com.minko.socket.dto.OrderItem orderItem);
//
  @Mappings({
      @Mapping(target = "id", ignore = true),
      @Mapping(target = "createdDate", expression = "java(java.time.Instant.now())"),
      @Mapping(target = "email", expression = "java(getAccount())"),
  })
  public abstract Order mapFromDtoToOrder(OrderRequestDto orderRequestDto);

  @Mapping(target = "createdDate", expression = "java(java.util.Date.from(order.getCreatedDate()))")
  public abstract OrderResponseDto mapToDto(Order order);

  @Mapping(target = "productId", source = "id")
  public abstract OrderItemResponseDto mapToOrderItemResponseDtoFromProduct(Product product);

  //
//    Product getProduct(Long id) {
//        return productService.getProductById(id);
//    }
//
  String getAccount() {
    return "alexander_ming@i.ua";
  }
}
