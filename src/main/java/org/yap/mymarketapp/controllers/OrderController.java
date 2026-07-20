package org.yap.mymarketapp.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.yap.mymarketapp.services.OrderService;
import reactor.core.publisher.Mono;

@Controller
@RequestMapping("/orders")
public class OrderController {

    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    @GetMapping()
    public Mono<String> getAll(Model model) {
        return service.getAll()
                .collectList()
                .flatMap(orders -> {
                    model.addAttribute("orders", orders);
                    return Mono.just("orders");
                });
    }

    @GetMapping("/{id}")
    public Mono<String> getOrCreateOrder(@PathVariable long id,
                                         @RequestParam(required = false) boolean newOrder,
                                         Model model) {
        return service.getById(id)
                .flatMap(order -> {
                    model.addAttribute("order", order);
                    model.addAttribute("newOrder", newOrder);
                    return Mono.just("order");
                });
    }

}
