package org.yap.mymarketapp.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.yap.mymarketapp.dtos.*;
import org.yap.mymarketapp.services.CartService;
import org.yap.mymarketapp.services.ItemService;
import reactor.core.publisher.Mono;


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
    public Mono<String> searchItems(@RequestParam(required = false) String search,
                                    @RequestParam(required = false, defaultValue = "NO") SortFieldEnum sort,
                                    @RequestParam(required = false, defaultValue = "0") int pageNumber,
                                    @RequestParam(required = false, defaultValue = "5") int pageSize,
                                    Model model) {

        return service.getAll(new SearchRequest(
                        search,
                        sort,
                        pageNumber,
                        pageSize))
                .flatMap(response -> {
                    model.addAttribute("items", response.items());
                    model.addAttribute("search", response.search());
                    model.addAttribute("sort", response.sort().toString());
                    model.addAttribute("paging", response.paging());
                    return Mono.just("items");
                });
    }

    /// Увеличение или уменьшение количества товара в корзине со страницы товаров в корзине
    @PostMapping("/items")
    public Mono<String> itemsToCart(@RequestParam(required = true) long id,
                                    @RequestParam(required = false) String search,
                                    @RequestParam(required = false) SortFieldEnum sort,
                                    @RequestParam(required = false) Integer pageNumber,
                                    @RequestParam(required = false) Integer pageSize,
                                    @RequestParam(required = true) CartActionEnum action) {

        return cartService.toCart(id, action)
                .then(Mono.fromSupplier(() -> {
                    StringBuilder url = new StringBuilder("redirect:/items");
                    String separator = "?";

                    if (search != null) {
                        url.append(separator).append("search=").append(search);
                        separator = "&";
                    }
                    if (sort != null) {
                        url.append(separator).append("sort=").append(sort);
                        separator = "&";
                    }
                    if (pageNumber != null) {
                        url.append(separator).append("pageNumber=").append(pageNumber);
                        separator = "&";
                    }
                    if (pageSize != null) {
                        url.append(separator).append("pageSize=").append(pageSize);
                    }

                    return url.toString();
                }));
    }

    /// Уменьшение/увеличение количества товара в корзине со страницы товара в корзине
    @PostMapping("items/{id}")
    public Mono<String> itemsToCart(@PathVariable long id, @RequestParam CartActionEnum action) {
        return cartService.toCart(id, action)
                .thenReturn(String.format("redirect:/items/%d", id));
    }

    /// Получение страницы с товаром
    @GetMapping("items/{id}")
    public Mono<String> getItem(@PathVariable long id, Model model) {
        return service.getById(id)
                .flatMap(item -> {
                    model.addAttribute("item", item);
                    return Mono.just("item");
                });
    }

}
