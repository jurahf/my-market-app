package org.yap.mymarketapp.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class ItemController {

    @GetMapping("/test")
    public String test() {
        logger.info("Test method called, returning view: test");
        return "cart";
    }

    private static final Logger logger = LoggerFactory.getLogger(ItemController.class);

}
