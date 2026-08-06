package org.yap.mymarketapp.handlers;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.yap.mymarketapp.services.OrderService;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
public class OrderHandler {

    private final OrderService service;

    public OrderHandler(OrderService service) {
        this.service = service;
    }

    public Mono<ServerResponse> getAll(ServerRequest request) {
        return service.getAll()
                .collectList()
                .flatMap(orders -> ServerResponse.ok()
                        .render("orders", Map.of("orders", orders)));
    }

    public Mono<ServerResponse> getOrCreateOrder(ServerRequest request) {
        var id = Long.parseLong(request.pathVariable("id"));
        var newOrder = Boolean.parseBoolean(request.queryParam("newOrder").orElse("false"));
        return service.getById(id)
                .flatMap(order -> ServerResponse.ok()
                        .render("order", Map.of(
                                "order", order,
                                "newOrder", newOrder
                        )));
    }
}
