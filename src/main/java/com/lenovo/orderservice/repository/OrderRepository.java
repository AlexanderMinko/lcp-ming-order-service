package com.lenovo.orderservice.repository;

import java.util.List;

import com.lenovo.orderservice.entity.Order;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends MongoRepository<Order, String> {

  List<Order> findByEmail(String email);
}
