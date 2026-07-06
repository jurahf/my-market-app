package org.yap.mymarketapp.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.yap.mymarketapp.dtos.*;

import java.util.List;

@Controller
public class ItemController {


    /// Получение списка товаров, список списков по три штуки
    @GetMapping({"/", "/items"})
    public List<List<ItemDto>> searchItems(@RequestParam String search,
                                           @RequestParam SortFieldEnum sort,
                                           @RequestParam int pageNumber,
                                           @RequestParam int pageSize) {
        // TODO: вычитать из базы

        return List.of(
                List.of(new ItemDto(), new ItemDto(), new ItemDto())
        );
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
