package com.lenovo.orderservice;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
public class OrderServiceApplication {

    public static void main(String[] args) {
        var context = SpringApplication.run(OrderServiceApplication.class, args);
    }

}