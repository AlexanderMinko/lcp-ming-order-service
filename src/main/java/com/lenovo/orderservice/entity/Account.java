package com.lenovo.orderservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Account {
  private String accountId;
  private String username;
  private String email;
  private String firstName;
  private String lastName;
}
