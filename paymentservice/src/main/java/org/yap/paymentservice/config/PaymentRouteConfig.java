package org.yap.paymentservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.yap.paymentservice.handlers.PaymentHandler;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class PaymentRouteConfig {

    @Bean
    public RouterFunction<ServerResponse> paymentRoutes(PaymentHandler handler) {
        return route()
                .GET("/api/balance", handler::getBalance)
                .build();
    }
}