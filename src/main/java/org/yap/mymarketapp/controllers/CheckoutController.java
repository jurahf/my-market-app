package org.yap.mymarketapp.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.yap.mymarketapp.services.CheckoutService;


@Controller
public class CheckoutController {

    private final CheckoutService service;

    public CheckoutController(CheckoutService service) {
        this.service = service;
    }

    @PostMapping("/buy")
    public String buy() {
        long id = service.createOrder();

        return String.format("redirect:/orders/%d?newOrder=true", id);
    }

}
