package org.yap.mymarketapp.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.yap.mymarketapp.dtos.CartActionEnum;
import org.yap.mymarketapp.services.CartService;
import reactor.core.publisher.Mono;

@Controller
@RequestMapping("/cart")
public class CartController {

    private final CartService service;

    public CartController(CartService service) {
        this.service = service;
    }


    /// Получение страницы со списком товаров в корзине
    @GetMapping("/items")
    public Mono<String> getCart(Model model) {
        return service.getItemsInCart()
                .flatMap(response -> {
                    model.addAttribute("items", response.getItems());
                    model.addAttribute("total", response.getTotal());
                    return Mono.just("cart");
                });
    }

    /// Уменьшение/увеличение количества товара в корзине со страницы корзины
    @PostMapping("/items")
    public Mono<String> itemsToCart(@RequestParam long id, @RequestParam CartActionEnum action) {
        return service.toCart(id, action)
                .thenReturn("redirect:/cart/items");
    }
}
