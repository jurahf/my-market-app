package org.yap.mymarketapp.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.yap.mymarketapp.config.ItemRouteConfig;
import org.yap.mymarketapp.dtos.*;
import org.yap.mymarketapp.handlers.ItemHandler;
import org.yap.mymarketapp.handlers.TemplateModelHelper;
import org.yap.mymarketapp.services.CartService;
import org.yap.mymarketapp.services.ItemService;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebFluxTest
@Import({ItemHandler.class, ItemRouteConfig.class, TemplateModelHelper.class})
class ItemControllerTests {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private ItemService itemService;

    @MockitoBean
    private CartService cartService;

    @Test
    void searchItems_shouldReturnItemsViewWithModelAttributes() {
        var paging = new PagingDto(0, 5, false, false);
        var itemDto = new ItemDto(1L, "Item1", "Desc1", "/img.png", 100L, 0);
        var response = new SearchResponse("test", SortFieldEnum.ALPHA, paging, List.of(List.of(itemDto)));

        when(itemService.getAll(any(SearchRequest.class))).thenReturn(Mono.just(response));

        webTestClient.get().uri(uriBuilder -> uriBuilder
                        .path("/items")
                        .queryParam("search", "test")
                        .queryParam("sort", "ALPHA")
                        .queryParam("pageNumber", 0)
                        .queryParam("pageSize", 5)
                        .build())
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void searchItems_withDefaultParameters_shouldReturnItemsView() {
        var paging = new PagingDto(0, 5, false, false);
        var itemDto = new ItemDto(1L, "Item1", "Desc1", "/img.png", 100L, 0);
        var response = new SearchResponse("item", SortFieldEnum.ALPHA, paging, List.of(List.of(itemDto)));

        when(itemService.getAll(any(SearchRequest.class))).thenReturn(Mono.just(response));

        webTestClient.get().uri("/")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void itemsToCart_withRequiredParametersOnly_shouldRedirectToItems() {
        when(cartService.toCart(1L, CartActionEnum.PLUS)).thenReturn(Mono.empty());

        webTestClient.post().uri(uriBuilder -> uriBuilder
                        .path("/items")
                        .queryParam("id", 1)
                        .queryParam("action", "PLUS")
                        .build())
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueEquals("Location", "/items");

        verify(cartService).toCart(1L, CartActionEnum.PLUS);
    }

    @Test
    void itemsToCart_withPathId_shouldRedirectToItemPage() {
        when(cartService.toCart(1L, CartActionEnum.PLUS)).thenReturn(Mono.empty());

        webTestClient.post().uri(uriBuilder -> uriBuilder
                        .path("/items/1")
                        .queryParam("action", "PLUS")
                        .build())
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueEquals("Location", "/items/1");

        verify(cartService).toCart(1L, CartActionEnum.PLUS);
    }

    @Test
    void getItem_shouldReturnItemViewWithItemModel() {
        var item = new ItemDto(1L, "Test Item", "Desc", "/img", 100L, 5);
        when(itemService.getById(1L)).thenReturn(Mono.just(item));

        webTestClient.get().uri("/items/1")
                .exchange()
                .expectStatus().isOk();
    }

}
