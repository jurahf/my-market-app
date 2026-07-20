package org.yap.mymarketapp.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.yap.mymarketapp.services.CheckoutService;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebFluxTest(CheckoutController.class)
class CheckoutControllerTests {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private CheckoutService checkoutService;

    @Test
    void buy_shouldCreateOrderAndRedirectToOrderPage() {
        long orderId = 123L;
        when(checkoutService.createOrder()).thenReturn(Mono.just(orderId));

        webTestClient.post().uri("/buy")
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueEquals("Location", "/orders/123?newOrder=true");

        verify(checkoutService).createOrder();
    }

    @Test
    void buy_whenOrderCreatedWithDifferentId_shouldRedirectWithCorrectId() {
        long orderId = 456L;
        when(checkoutService.createOrder()).thenReturn(Mono.just(orderId));

        webTestClient.post().uri("/buy")
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueEquals("Location", "/orders/456?newOrder=true");

        verify(checkoutService).createOrder();
    }

    @Test
    void buy_shouldReturnRedirectStatus() {
        when(checkoutService.createOrder()).thenReturn(Mono.just(1L));

        webTestClient.post().uri("/buy")
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueMatches("Location", "/orders/\\d+\\?newOrder=true");

        verify(checkoutService).createOrder();
    }
}
