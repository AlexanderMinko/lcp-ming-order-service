package com.lenovo.orderservice.integration;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.security.oauth2.core.OAuth2AccessToken.TokenType.BEARER;

import java.util.List;
import java.util.UUID;

import com.lenovo.orderservice.config.AppConfig;
import com.lenovo.orderservice.dto.OrderItemRequestDto;
import com.lenovo.orderservice.dto.OrderRequestDto;
import com.lenovo.orderservice.entity.Account;
import com.lenovo.orderservice.entity.Order;
import com.lenovo.orderservice.entity.OrderItem;
import com.lenovo.orderservice.entity.Product;
import com.lenovo.orderservice.repository.OrderRepository;
import com.lenovo.service.EventProducerService;
import com.lenovo.service.LcpJwtService;
import com.lenovo.test.InjectJwt;
import com.lenovo.test.IntegrationTest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;
import lombok.Value;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
@InjectJwt
@TestPropertySource(properties = {
    "product-service.url=${wiremock.server.host}",
    "spring.security.oauth2.client.registration.keycloak.client-secret=order-service-secret"
})
public class OrderServiceIntegrationTest {

  private static final ObjectMapper MAPPER = new ObjectMapper();
  private static final String UUID1 = UUID.randomUUID().toString();
  private static final String UUID2 = UUID.randomUUID().toString();
  private static final String UUID3 = UUID.randomUUID().toString();
  private static final String ORDER_SERVICE_URL = "/orders";

  @Autowired
  private WebTestClient webTestClient;

  @Autowired
  private OrderRepository orderRepository;

  @Autowired
  private AppConfig config;

  @Autowired
  private EventProducerService eventProducerService;

  @Autowired
  private LcpJwtService lcpJwtService;

  @BeforeAll
  static void beforeAll() {
    MAPPER.registerModule(new JavaTimeModule());
  }

  @AfterEach
  void tearDown() {
    orderRepository.deleteAll();
  }

  //-----------------------------------------GET list-------------------------------------------------------------------

  @Test
  void shouldReturnListOfOrders() throws JsonProcessingException {
    orderRepository.saveAll(stubOrders());
    webTestClient
        .get()
        .uri(ORDER_SERVICE_URL + "/list")
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .json(MAPPER.writeValueAsString(stubOrders()));
  }

  //-----------------------------------------POST make------------------------------------------------------------------

  @Test
  void shouldMakeOrder() {
    stubOAuth2ServerResponse();
    stubProductServiceResponse();
    webTestClient
        .post()
        .uri(ORDER_SERVICE_URL + "/make")
        .bodyValue(stubOrderRequest())
        .exchange()
        .expectStatus()
        .isCreated();

    var actual = orderRepository.findAll().get(0);
    assertThat(actual).isNotNull();
    assertThat(actual.getOrderItems().size()).isEqualTo(stubOrderRequest().getOrderItemRequestsDto().size());
  }

  //-----------------------------------------GET {id}-------------------------------------------------------------------

  @Test
  void shouldReturnOrderByOrderId() {
    stubOAuth2ServerResponse();
    stubProductServiceResponse();
    var savedOrder = orderRepository.save(stubOrder());
    webTestClient
        .get()
        .uri(uriBuilder -> uriBuilder.path(ORDER_SERVICE_URL + "/{id}/items").build(savedOrder.getId()))
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.[0].productId").isEqualTo(savedOrder.getOrderItems().get(0).getProductId());
  }

  //-----------------------------------------Utils----------------------------------------------------------------------

  private OrderRequestDto stubOrderRequest() {
    var orderItemRequestDto1 = OrderItemRequestDto.builder()
        .productId(UUID1)
        .count(1)
        .build();

    var orderItemRequestDto2 = OrderItemRequestDto.builder()
        .productId(UUID2)
        .count(1)
        .build();

    var orderItemRequestDto3 = OrderItemRequestDto.builder()
        .productId(UUID3)
        .count(1)
        .build();
    return new OrderRequestDto(List.of(orderItemRequestDto1, orderItemRequestDto2, orderItemRequestDto3));
  }

  private List<Order> stubOrders() {
    var account = Account.builder()
        .accountId(UUID1)
        .username("alex")
        .email("lol@lol.com")
        .firstName("alex")
        .lastName("ming")
        .build();
    var order = Order.builder()
        .id(UUID1)
        .email("lol@lol.com")
        .account(account)
        .orderItems(List.of())
        .build();
    return List.of(order);
  }

  private Order stubOrder() {
    var account = Account.builder()
        .accountId(UUID1)
        .username("alex")
        .email("lol@lol.com")
        .firstName("alex")
        .lastName("ming")
        .build();
    return Order.builder()
        .id(UUID1)
        .email("lol@lol.com")
        .account(account)
        .orderItems(List.of(OrderItem.builder().productId(UUID1).count(1).build()))
        .build();
  }

  private List<Product> stubProducts() {
    var product1 = Product.builder()
        .id(UUID1)
        .name("Crash Course in Python")
        .description("Learn Python at your own pace")
        .imageUrl("assets/images/products/books/book-1000.png")
        .price(14.99)
        .build();
    var product2 = Product.builder()
        .id(UUID2)
        .name("Become a Guru in JavaScript")
        .description("Learn JavaScript at your own pace.")
        .imageUrl("assets/images/products/books/book-1001.png")
        .price(20.99).build();
    var product3 = Product.builder()
        .id(UUID3)
        .name("Exploring Vue.js")
        .description("Learn Vue.js at your own pace")
        .imageUrl("assets/images/products/books/book-1002.png")
        .price(13.99)
        .build();
    return List.of(product1, product2, product3);
  }

  @SneakyThrows
  private void stubProductServiceResponse() {
    stubFor(post(urlEqualTo("/products/by-ids"))
        .willReturn(aResponse()
            .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
            .withBody(MAPPER.writeValueAsString(stubProducts()))
            .withStatus(200)));
  }

  private static final String ACCESS_TOKEN = randomAlphanumeric(100);
  private static final String TOKEN_URI = "/auth/realms/LCPRealm/protocol/openid-connect/token";

  @SneakyThrows
  public void stubOAuth2ServerResponse() {
    var tokenResponse = new OAuth2Token(ACCESS_TOKEN, BEARER.getValue());

    stubFor(post(urlEqualTo(TOKEN_URI))
        .withBasicAuth("lenovo-client", "order-service-secret")
        .withRequestBody(equalTo("grant_type=" + "client_credentials"))
        .willReturn(aResponse()
            .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
            .withBody(MAPPER.writeValueAsString(tokenResponse))));
  }

  @Value
  private static class OAuth2Token {
    String access_token;
    String token_type;
  }
}
