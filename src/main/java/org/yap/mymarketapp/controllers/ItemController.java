package org.yap.mymarketapp.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.yap.mymarketapp.dtos.*;
import org.yap.mymarketapp.services.CartService;
import org.yap.mymarketapp.services.ItemService;

import java.util.Optional;

@Controller
public class ItemController {

    private final ItemService service;

    private final CartService cartService;

    public ItemController(ItemService service, CartService cartService) {
        this.service = service;
        this.cartService = cartService;
    }

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

        modelAndView.addObject("items", response.items());
        modelAndView.addObject("search", response.search());
        modelAndView.addObject("sort", response.sort().toString());
        modelAndView.addObject("paging", response.paging());

        return modelAndView;
    }

    /// Увеличение или уменьшение количества товара в корзине со страницы товаров в корзине
    @PostMapping("/items")
    public String itemsToCart(@RequestParam(required = true) long id,
                              @RequestParam(required = false) String search,
                              @RequestParam(required = false) SortFieldEnum sort,
                              @RequestParam(required = false) Integer pageNumber,
                              @RequestParam(required = false) Integer pageSize,
                              @RequestParam(required = true) CartActionEnum action,
                              RedirectAttributes redirectAttributes) {
        cartService.toCart(id, action);

        if (search != null) {
            redirectAttributes.addAttribute("search", search);
        }
        if (sort != null) {
            redirectAttributes.addAttribute("sort", sort);
        }
        if (pageNumber != null) {
            redirectAttributes.addAttribute("pageNumber", pageNumber);
        }
        if (pageSize != null) {
            redirectAttributes.addAttribute("pageSize", pageSize);
        }

        return String.format("redirect:/items");
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
