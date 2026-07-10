package org.yap.mymarketapp.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.yap.mymarketapp.dtos.CartActionEnum;
import org.yap.mymarketapp.dtos.CartResponse;
import org.yap.mymarketapp.services.CartService;

@Controller
@RequestMapping("/cart")
public class CartController {

    private final CartService service;

    public CartController(CartService service) {
        this.service = service;
    }

    /// Получение страницы со списком товаров в корзине
    @GetMapping("/items")
    public ModelAndView getCart() {

        CartResponse response = service.getItemsInCart();

        ModelAndView modelAndView = new ModelAndView("cart");

        modelAndView.addObject("items", response.getItems());
        modelAndView.addObject("total", response.getTotal());

        return modelAndView;
    }

    /// Уменьшение/увеличение количества товара в корзине со страницы корзины
    @PostMapping("/items")
    public String itemsToCart(@RequestParam long id, @RequestParam CartActionEnum action) {
        service.toCart(id, action);

        return "redirect:/cart/items";
    }
}
