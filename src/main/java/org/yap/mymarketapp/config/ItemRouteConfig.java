package org.yap.mymarketapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.yap.mymarketapp.handlers.ItemHandler;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class ItemRouteConfig {

    @Bean
    public RouterFunction<ServerResponse> itemRoutes(ItemHandler handler) {
        return route()
                .GET("/", handler::searchItems)
                .GET("/items", handler::searchItems)
                .POST("/items", handler::itemsToCart)
                .GET("/items/{id}", handler::getItem)
                .POST("/items/{id}", handler::itemsToCartWithPath)
                .build();
    }
}
