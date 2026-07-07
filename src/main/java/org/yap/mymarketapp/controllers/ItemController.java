package org.yap.mymarketapp.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.yap.mymarketapp.dtos.*;
import org.yap.mymarketapp.repositories.ItemRepository;
import org.yap.mymarketapp.services.ItemService;

import java.util.List;
import java.util.Optional;

@Controller
public class ItemController {

    @Autowired
    public ItemService service;

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
        modelAndView.addObject("sort", response.getSort());
        modelAndView.addObject("paging", response.getPaging());

        return modelAndView;
    }

    /// Увеличение или уменьшение количества товара в корзине со страницы товаров в корзине
    @PostMapping("/items")
    public String itemsToCart(@RequestParam long id,
                              @RequestParam String search,
                              @RequestParam SortFieldEnum sort,
                              @RequestParam int pageNumber,
                              @RequestParam int pageSize,
                              @RequestParam CartActionEnum action) {
        // TODO: изменить количество товара в корзине

        return "redirect:/items?search=[search]&sort=[sort]&pageNumber=[pageNumber]&pageSize=[pageSize]";
    }

    /// уменьшения/увеличения количества товара в корзине со страницы товара в корзине
    @PostMapping("items/{id}")
    public String itemsToCart(@PathVariable long id, @RequestParam CartActionEnum action) {
        // TODO: изменить количество товара в корзине

        return getItem(id);
    }

    ///
    @GetMapping("items/{id}")
    public String getItem(@PathVariable long id) {
//        Атрибуты модели: (= ItemDTO)
//        item — объект товара со следующими полями:
//        long id — идентификатор товара,
//        String title — название товара,
//        String description — описание товара,
//        String imgPath — путь к изображению товара,
//        long price — цена товара,
//        int count — число товаров в корзине (если 0, значит, не положен в корзину).

        // TODO: заполнить модель
        return "item";
    }

}
