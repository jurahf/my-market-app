package org.yap.mymarketapp.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;
import org.yap.mymarketapp.dtos.CartResponse;
import org.yap.mymarketapp.dtos.OrderDto;
import org.yap.mymarketapp.services.OrderService;

import java.util.List;

@Controller
public class OrderController {

    @Autowired
    public OrderService service;


    /// Получение страницы со списком заказов
    @GetMapping("/orders")
    public ModelAndView getAll() {

        List<OrderDto> orders = service.getAll();

        ModelAndView modelAndView = new ModelAndView("orders");

        modelAndView.addObject("orders", orders);

        return modelAndView;
    }

}
