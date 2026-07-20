package org.yap.mymarketapp.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.server.ResponseStatusException;
import org.yap.mymarketapp.dtos.ItemDto;
import org.yap.mymarketapp.dtos.OrderDto;
import org.yap.mymarketapp.services.OrderService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.mockito.Mockito.when;

@WebFluxTest(OrderController.class)
class OrderControllerTests {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private OrderService orderService;

    @Test
    void getAll_shouldReturnOrdersViewWithOrdersList() {
        var orderItems = List.of(
                new ItemDto(1L, "Item1", "", "", 100L, 2),
                new ItemDto(2L, "Item2", "", "", 50L, 1)
        );
        var orders = List.of(
                new OrderDto(1L, orderItems, 250L),
                new OrderDto(2L, orderItems, 150L)
        );

        when(orderService.getAll()).thenReturn(Flux.fromIterable(orders));

        webTestClient.get().uri("/orders")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void getOrCreateOrder_withoutNewOrderParam_shouldReturnOrderViewWithOrder() {
        var orderItems = List.of(
                new ItemDto(1L, "Item1", "", "", 100L, 2)
        );
        var order = new OrderDto(1L, orderItems, 200L);

        when(orderService.getById(1L)).thenReturn(Mono.just(order));

        webTestClient.get().uri("/orders/1")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void getOrCreateOrder_withNewOrderTrue_shouldReturnOrderViewWithNewOrderFlag() {
        var orderItems = List.of(
                new ItemDto(1L, "Item1", "", "", 100L, 2)
        );
        var order = new OrderDto(1L, orderItems, 200L);

        when(orderService.getById(1L)).thenReturn(Mono.just(order));

        webTestClient.get().uri(uriBuilder -> uriBuilder
                        .path("/orders/1")
                        .queryParam("newOrder", true)
                        .build())
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void getOrCreateOrder_withNonExistentId_shouldReturnErrorPage() {
        when(orderService.getById(999L))
                .thenReturn(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)));

        webTestClient.get().uri("/orders/999")
                .exchange()
                .expectStatus().is4xxClientError();
    }
}
