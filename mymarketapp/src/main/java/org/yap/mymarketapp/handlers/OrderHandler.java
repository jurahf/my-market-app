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
    private final TemplateModelHelper templateModel;

    public OrderHandler(OrderService service, TemplateModelHelper templateModel) {
        this.service = service;
        this.templateModel = templateModel;
    }

    public Mono<ServerResponse> getAll(ServerRequest request) {
        return service.getAll()
                .collectList()
                .flatMap(orders -> templateModel.withSecurity(request, Map.of("orders", orders)))
                .flatMap(model -> ServerResponse.ok().render("orders", model));
    }

    public Mono<ServerResponse> getOrCreateOrder(ServerRequest request) {
        var id = Long.parseLong(request.pathVariable("id"));
        var newOrder = Boolean.parseBoolean(request.queryParam("newOrder").orElse("false"));
        return service.getById(id)
                .flatMap(order -> templateModel.withSecurity(request, Map.of(
                        "order", order,
                        "newOrder", newOrder)))
                .flatMap(model -> ServerResponse.ok().render("order", model));
    }
}
