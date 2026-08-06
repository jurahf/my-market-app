package org.yap.mymarketapp.handlers;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.yap.mymarketapp.dtos.CartActionEnum;
import org.yap.mymarketapp.services.CartService;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Map;
import java.util.Optional;

@Component
public class CartHandler {

    private final CartService service;

    public CartHandler(CartService service) {
        this.service = service;
    }

    public Mono<ServerResponse> getCart(ServerRequest request) {
        return service.getItemsInCart()
                .flatMap(response -> ServerResponse.ok()
                        .render("cart", Map.of(
                                "items", response.getItems(),
                                "total", response.getTotal(),
                                "balance", response.getBalance()
                        )));
    }

    public Mono<ServerResponse> itemsToCart(ServerRequest request) {
        return request.formData()
                .flatMap(formData -> {
                    var idParam = request.queryParam("id")
                            .or(() -> Optional.ofNullable(formData.getFirst("id")));
                    var actionParam = request.queryParam("action")
                            .or(() -> Optional.ofNullable(formData.getFirst("action")));
                    if (idParam.isEmpty() || actionParam.isEmpty()) {
                        return ServerResponse.badRequest().build();
                    }
                    try {
                        var id = Long.parseLong(idParam.get());
                        var action = CartActionEnum.valueOf(actionParam.get());
                        return service.toCart(id, action)
                                .then(ServerResponse.seeOther(URI.create("/cart/items")).build());
                    } catch (IllegalArgumentException e) {
                        return ServerResponse.badRequest().build();
                    }
                });
    }
}
