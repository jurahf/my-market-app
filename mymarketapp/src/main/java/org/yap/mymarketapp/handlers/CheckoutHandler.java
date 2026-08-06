package org.yap.mymarketapp.handlers;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.yap.mymarketapp.services.CheckoutService;
import reactor.core.publisher.Mono;

import java.net.URI;

@Component
public class CheckoutHandler {

    private final CheckoutService service;

    public CheckoutHandler(CheckoutService service) {
        this.service = service;
    }

    public Mono<ServerResponse> buy(ServerRequest request) {
        return service.createOrder()
                .flatMap(id -> ServerResponse.seeOther(
                        URI.create(String.format("/orders/%d?newOrder=true", id))).build());
    }
}
