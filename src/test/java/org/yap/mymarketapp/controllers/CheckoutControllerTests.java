package org.yap.mymarketapp.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.yap.mymarketapp.services.CheckoutService;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CheckoutController.class)
class CheckoutControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CheckoutService checkoutService;

    @Test
    void buy_shouldCreateOrderAndRedirectToOrderPage() throws Exception {
        long orderId = 123L;
        when(checkoutService.createOrder()).thenReturn(orderId);

        mockMvc.perform(post("/buy"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/orders/123?newOrder=true"));

        verify(checkoutService).createOrder();
    }

    @Test
    void buy_whenOrderCreatedWithDifferentId_shouldRedirectWithCorrectId() throws Exception {
        long orderId = 456L;
        when(checkoutService.createOrder()).thenReturn(orderId);

        mockMvc.perform(post("/buy"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/orders/456?newOrder=true"));

        verify(checkoutService).createOrder();
    }

    @Test
    void buy_shouldReturnRedirectStatus() throws Exception {
        when(checkoutService.createOrder()).thenReturn(1L);

        mockMvc.perform(post("/buy"))
                .andExpect(status().isFound()) // 302
                .andExpect(redirectedUrlPattern("/orders/*?newOrder=true"));

        verify(checkoutService).createOrder();
    }
}