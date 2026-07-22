package org.yap.mymarketapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.yap.mymarketapp.handlers.CartHandler;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class CartRouteConfig {

    @Bean
    public RouterFunction<ServerResponse> cartRoutes(CartHandler handler) {
        return route()
                .GET("/cart/items", handler::getCart)
                .POST("/cart/items", handler::itemsToCart)
                .build();
    }
}
