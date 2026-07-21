package org.yap.mymarketapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.yap.mymarketapp.handlers.CheckoutHandler;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class CheckoutRouteConfig {

    @Bean
    public RouterFunction<ServerResponse> checkoutRoutes(CheckoutHandler handler) {
        return route()
                .POST("/buy", handler::buy)
                .build();
    }
}
