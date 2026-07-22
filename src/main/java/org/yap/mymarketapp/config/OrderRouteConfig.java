package org.yap.mymarketapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.yap.mymarketapp.handlers.OrderHandler;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class OrderRouteConfig {

    @Bean
    public RouterFunction<ServerResponse> orderRoutes(OrderHandler handler) {
        return route()
                .GET("/orders", handler::getAll)
                .GET("/orders/{id}", handler::getOrCreateOrder)
                .build();
    }
}
