package org.yap.mymarketapp.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.yap.mymarketapp.dtos.CartActionEnum;
import org.yap.mymarketapp.dtos.CartResponse;
import org.yap.mymarketapp.dtos.ItemDto;
import org.yap.mymarketapp.services.CartService;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(CartController.class)
class CartControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CartService cartService;

    @Test
    void getCart_shouldReturnCartViewWithItemsAndTotal() throws Exception {
        // Given
        var items = List.of(
                new ItemDto(1L, "Item1", "Desc", "img", 100L, 2),
                new ItemDto(2L, "Item2", "", "", 50L, 3)
        );
        var cartResponse = new CartResponse(items, 350L);

        when(cartService.getItemsInCart()).thenReturn(cartResponse);

        // When & Then
        mockMvc.perform(get("/cart/items"))
                .andExpect(status().isOk())
                .andExpect(view().name("cart"))
                .andExpect(model().attributeExists("items", "total"))
                .andExpect(model().attribute("items", items))
                .andExpect(model().attribute("total", 350L));
    }

    @Test
    void getCart_whenCartIsEmpty_shouldReturnCartViewWithEmptyItemsAndZeroTotal() throws Exception {
        // Given
        var cartResponse = new CartResponse(List.of(), 0L);

        when(cartService.getItemsInCart()).thenReturn(cartResponse);

        // When & Then
        mockMvc.perform(get("/cart/items"))
                .andExpect(status().isOk())
                .andExpect(view().name("cart"))
                .andExpect(model().attribute("items", List.of()))
                .andExpect(model().attribute("total", 0L));
    }

    @Test
    void itemsToCart_withAddAction_shouldRedirectToCartItems() throws Exception {
        // When & Then
        mockMvc.perform(post("/cart/items")
                        .param("id", "1")
                        .param("action", "PLUS"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cart/items"));

        verify(cartService).toCart(1L, CartActionEnum.PLUS);
    }

    @Test
    void itemsToCart_withRemoveAction_shouldRedirectToCartItems() throws Exception {
        // When & Then
        mockMvc.perform(post("/cart/items")
                        .param("id", "2")
                        .param("action", "MINUS"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cart/items"));

        verify(cartService).toCart(2L, CartActionEnum.MINUS);
    }

    @Test
    void itemsToCart_withInvalidAction_shouldReturnBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(post("/cart/items")
                        .param("id", "1")
                        .param("action", "INVALID"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void itemsToCart_withoutIdParameter_shouldReturnBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(post("/cart/items")
                        .param("action", "ADD"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void itemsToCart_withoutActionParameter_shouldReturnBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(post("/cart/items")
                        .param("id", "1"))
                .andExpect(status().isBadRequest());
    }
}
