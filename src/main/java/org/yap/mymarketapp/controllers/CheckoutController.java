package org.yap.mymarketapp.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.yap.mymarketapp.services.CheckoutService;
import reactor.core.publisher.Mono;


@Controller
public class CheckoutController {

    private final CheckoutService service;

    public CheckoutController(CheckoutService service) {
        this.service = service;
    }

    @PostMapping("/buy")
    public Mono<String> buy() {
        return service.createOrder()
                .map(id -> String.format("redirect:/orders/%d?newOrder=true", id));
    }

}
