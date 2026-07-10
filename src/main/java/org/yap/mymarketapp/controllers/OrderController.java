package org.yap.mymarketapp.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.yap.mymarketapp.dtos.OrderDto;
import org.yap.mymarketapp.services.OrderService;

import java.util.List;

@Controller
public class OrderController {

    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    /// Получение страницы со списком заказов
    @GetMapping("/orders")
    public ModelAndView getAll() {

        List<OrderDto> orders = service.getAll();

        ModelAndView modelAndView = new ModelAndView("orders");

        modelAndView.addObject("orders", orders);

        return modelAndView;
    }


    /// Страница заказа
    @GetMapping("/orders/{id}")
    public ModelAndView getOrCreateOrder(@PathVariable long id, @RequestParam(required = false) boolean newOrder) {

        OrderDto order = service.getById(id);

        ModelAndView modelAndView = new ModelAndView("order");

        modelAndView.addObject("order", order);
        modelAndView.addObject("newOrder", newOrder);

        return modelAndView;
    }

    @PostMapping("/buy")
    public String buy() {
        long id = service.createOrder();

        return String.format("redirect:/orders/%d?newOrder=true", id);
    }


}
