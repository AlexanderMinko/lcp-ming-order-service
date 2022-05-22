package com.lenovo.orderservice.events;

import com.lenovo.orderservice.service.OrderService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class OrderConsumerService {

  private final OrderService orderService;

  @KafkaListener(topics = {"test-event"})
  public void consumeCreateOrderEvent(String event) {
    log.debug("Consumed event: {}", event);
//        var sendProductsEvent = deserialize(event, SendProductsEvent.class);
//        orderService.proceedMakeOrder(sendProductsEvent);
  }
}
