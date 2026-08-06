package org.yap.mymarketapp.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.yap.mymarketapp.config.CartRouteConfig;
import org.yap.mymarketapp.dtos.CartActionEnum;
import org.yap.mymarketapp.dtos.CartResponse;
import org.yap.mymarketapp.dtos.ItemDto;
import org.yap.mymarketapp.handlers.CartHandler;
import org.yap.mymarketapp.services.CartService;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@WebFluxTest
@Import({CartHandler.class, CartRouteConfig.class})
class CartControllerTests {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private CartService cartService;

    @Test
    void getCart_shouldReturnCartViewWithItemsAndTotal() {
        var items = List.of(
                new ItemDto(1L, "Item1", "Desc", "img", 100L, 2),
                new ItemDto(2L, "Item2", "", "", 50L, 3)
        );
        var cartResponse = new CartResponse(items, 350L, 400L);

        when(cartService.getItemsInCart()).thenReturn(Mono.just(cartResponse));

        webTestClient.get().uri("/cart/items")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void getCart_whenCartIsEmpty_shouldReturnCartViewWithEmptyItemsAndZeroTotal() {
        var cartResponse = new CartResponse(List.of(), 0L, 400L);

        when(cartService.getItemsInCart()).thenReturn(Mono.just(cartResponse));

        webTestClient.get().uri("/cart/items")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void itemsToCart_withAddAction_shouldRedirectToCartItems() {
        when(cartService.toCart(1L, CartActionEnum.PLUS)).thenReturn(Mono.empty());

        webTestClient.post().uri(uriBuilder -> uriBuilder
                        .path("/cart/items")
                        .queryParam("id", 1)
                        .queryParam("action", "PLUS")
                        .build())
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueEquals("Location", "/cart/items");

        verify(cartService).toCart(1L, CartActionEnum.PLUS);
    }

    @Test
    void itemsToCart_withRemoveAction_shouldRedirectToCartItems() {
        when(cartService.toCart(2L, CartActionEnum.MINUS)).thenReturn(Mono.empty());

        webTestClient.post().uri(uriBuilder -> uriBuilder
                        .path("/cart/items")
                        .queryParam("id", 2)
                        .queryParam("action", "MINUS")
                        .build())
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueEquals("Location", "/cart/items");

        verify(cartService).toCart(2L, CartActionEnum.MINUS);
    }

    @Test
    void itemsToCart_withInvalidAction_shouldReturnBadRequest() {
        webTestClient.post().uri(uriBuilder -> uriBuilder
                        .path("/cart/items")
                        .queryParam("id", 1)
                        .queryParam("action", "INVALID")
                        .build())
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void itemsToCart_withoutIdParameter_shouldReturnBadRequest() {
        webTestClient.post().uri(uriBuilder -> uriBuilder
                        .path("/cart/items")
                        .queryParam("action", "PLUS")
                        .build())
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void itemsToCart_withoutActionParameter_shouldReturnBadRequest() {
        webTestClient.post().uri(uriBuilder -> uriBuilder
                        .path("/cart/items")
                        .queryParam("id", 1)
                        .build())
                .exchange()
                .expectStatus().isBadRequest();
    }
}
