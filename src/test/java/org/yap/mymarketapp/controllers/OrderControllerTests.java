package org.yap.mymarketapp.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;
import org.yap.mymarketapp.dtos.ItemDto;
import org.yap.mymarketapp.dtos.OrderDto;
import org.yap.mymarketapp.services.OrderService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
class OrderControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

    @Test
    void getAll_shouldReturnOrdersViewWithOrdersList() throws Exception {
        var orderItems = List.of(
                new ItemDto(1L, "Item1", "", "", 100L, 2),
                new ItemDto(2L, "Item2", "", "",50, 1)
        );
        var orders = List.of(
                new OrderDto(1L, orderItems, 250L),
                new OrderDto(2L, orderItems, 150L)
        );

        when(orderService.getAll()).thenReturn(orders);

        mockMvc.perform(get("/orders"))
                .andExpect(status().isOk())
                .andExpect(view().name("orders"))
                .andExpect(model().attributeExists("orders"))
                .andExpect(model().attribute("orders", orders));
    }

    @Test
    void getOrCreateOrder_withoutNewOrderParam_shouldReturnOrderViewWithOrder() throws Exception {
        var orderItems = List.of(
                new ItemDto(1L, "Item1", "", "", 100L, 2)
        );
        var order = new OrderDto(1L, orderItems, 200L);

        when(orderService.getById(1L)).thenReturn(order);

        mockMvc.perform(get("/orders/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("order"))
                .andExpect(model().attributeExists("order", "newOrder"))
                .andExpect(model().attribute("order", order))
                .andExpect(model().attribute("newOrder", false));
    }

    @Test
    void getOrCreateOrder_withNewOrderTrue_shouldReturnOrderViewWithNewOrderFlag() throws Exception {
        var orderItems = List.of(
                new ItemDto(1L, "Item1", "", "", 100L, 2)
        );
        var order = new OrderDto(1L, orderItems, 200L);

        when(orderService.getById(1L)).thenReturn(order);

        mockMvc.perform(get("/orders/1")
                        .param("newOrder", "true"))
                .andExpect(status().isOk())
                .andExpect(view().name("order"))
                .andExpect(model().attribute("order", order))
                .andExpect(model().attribute("newOrder", true));
    }

    @Test
    void getOrCreateOrder_withNonExistentId_shouldReturnErrorPage() throws Exception {
        when(orderService.getById(999L)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        mockMvc.perform(get("/orders/999"))
                .andExpect(status().is4xxClientError());
    }
}