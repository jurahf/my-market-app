package org.yap.paymentservice.handlers;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.yap.paymentservice.services.PaymentService;
import reactor.core.publisher.Mono;

@Component
public class PaymentHandler {

    private final PaymentService service;

    public PaymentHandler(PaymentService service) {
        this.service = service;
    }

    public Mono<ServerResponse> getBalance(ServerRequest request) {
        //var id = Long.parseLong(request.queryParam("id").get());
        return service.getBalance()
                .flatMap(balance -> ServerResponse.ok().bodyValue(balance));
    }
}