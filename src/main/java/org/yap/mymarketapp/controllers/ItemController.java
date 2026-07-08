package org.yap.mymarketapp.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.yap.mymarketapp.dtos.*;
import org.yap.mymarketapp.services.CartService;
import org.yap.mymarketapp.services.ItemService;

import java.util.List;
import java.util.Optional;

@Controller
public class ItemController {

    @Autowired
    public ItemService service;

    @Autowired
    public CartService cartService;

    /// Получение списка товаров, список списков по три штуки
    @GetMapping({"/", "/items"})
    public ModelAndView searchItems(@RequestParam(required = false) String search,
                                           @RequestParam(required = false) Optional<SortFieldEnum> sort,
                                           @RequestParam(required = false) Optional<Integer> pageNumber,
                                           @RequestParam(required = false) Optional<Integer> pageSize) {

        var response = service.getAll(new SearchRequest(
                search,
                sort.orElseGet(() -> SortFieldEnum.NO),
                pageNumber.orElseGet(() -> 0),
                pageSize.orElseGet(() -> 5)));

        ModelAndView modelAndView = new ModelAndView("items");

        modelAndView.addObject("items", response.getItems());
        modelAndView.addObject("search", response.getSearch());
        modelAndView.addObject("sort", response.getSort().toString());
        modelAndView.addObject("paging", response.getPaging());

        return modelAndView;
    }

    /// Увеличение или уменьшение количества товара в корзине со страницы товаров в корзине
    @PostMapping("/items")
    public String itemsToCart(@RequestParam(required = true) long id,
                              @RequestParam(required = false) String search,
                              @RequestParam(required = false) SortFieldEnum sort,
                              @RequestParam(required = false) Integer pageNumber,
                              @RequestParam(required = false) Integer pageSize,
                              @RequestParam(required = true) CartActionEnum action) {
        cartService.toCart(id, action);

        return String.format("redirect:/items?search=%s&sort=%s&pageNumber=%d&pageSize=%d", search, sort, pageNumber, pageSize);
    }

    /// Уменьшение/увеличение количества товара в корзине со страницы товара в корзине
    @PostMapping("items/{id}")
    public String itemsToCart(@PathVariable long id, @RequestParam CartActionEnum action) {
        cartService.toCart(id, action);

        return String.format("redirect:/items/%d", id);
    }

    /// Получение страницы с товаром
    @GetMapping("items/{id}")
    public ModelAndView getItem(@PathVariable long id) {
        ItemDto item = service.getById(id);

        ModelAndView modelAndView = new ModelAndView("item");

        modelAndView.addObject("item", item);

        return modelAndView;
    }

}
