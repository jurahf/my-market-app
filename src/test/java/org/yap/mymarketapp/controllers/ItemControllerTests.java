package org.yap.mymarketapp.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.yap.mymarketapp.dtos.*;
import org.yap.mymarketapp.services.CartService;
import org.yap.mymarketapp.services.ItemService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
class ItemControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ItemService itemService;

    @MockitoBean
    private CartService cartService;

    @Test
    void searchItems_shouldReturnItemsViewWithModelAttributes() throws Exception {
        var paging = new PagingDto(0, 5, false, false);
        var itemDto = new ItemDto(1L, "Item1", "Desc1", "/img.png", 100L, 0);
        var response = new SearchResponse("test", SortFieldEnum.ALPHA, paging, List.of(List.of(itemDto)));

        when(itemService.getAll(any(SearchRequest.class))).thenReturn(response);

        mockMvc.perform(get("/items")
                        .param("search", "test")
                        .param("sort", "ALPHA")
                        .param("pageNumber", "0")
                        .param("pageSize", "5"))
                .andExpect(status().isOk())
                .andExpect(view().name("items"))
                .andExpect(model().attributeExists("items", "search", "sort", "paging"))
                .andExpect(model().attribute("items", List.of(List.of(itemDto))))
                .andExpect(model().attribute("search", "test"))
                .andExpect(model().attribute("sort", "ALPHA"))
                .andExpect(model().attribute("paging", paging));
    }

    @Test
    void searchItems_withDefaultParameters_shouldReturnItemsView() throws Exception {
        var paging = new PagingDto(0, 5, false, false);
        var itemDto = new ItemDto(1L, "Item1", "Desc1", "/img.png", 100L, 0);
        var response = new SearchResponse("item", SortFieldEnum.ALPHA, paging, List.of(List.of(itemDto)));

        when(itemService.getAll(any(SearchRequest.class))).thenReturn(response);

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("items"))
                .andExpect(model().attributeExists("items", "search", "sort", "paging"))
                .andExpect(model().attribute("search", "item"))
                .andExpect(model().attribute("sort", "ALPHA"));
    }

    @Test
    void itemsToCart_withRequiredParametersOnly_shouldRedirectToItems() throws Exception {
        mockMvc.perform(post("/items")
                        .param("id", "1")
                        .param("action", "PLUS"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/items"));

        verify(cartService).toCart(1L, CartActionEnum.PLUS);
    }

    @Test
    void itemsToCart_withPathId_shouldRedirectToItemPage() throws Exception {
        mockMvc.perform(post("/items/1")
                        .param("action", "PLUS"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/items/1"));

        verify(cartService).toCart(1L, CartActionEnum.PLUS);
    }

    @Test
    void getItem_shouldReturnItemViewWithItemModel() throws Exception {
        var item = new ItemDto(1L, "Test Item", "Desc", "/img", 100L, 5);
        when(itemService.getById(1L)).thenReturn(item);

        mockMvc.perform(get("/items/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("item"))
                .andExpect(model().attributeExists("item"))
                .andExpect(model().attribute("item", item));
    }

}