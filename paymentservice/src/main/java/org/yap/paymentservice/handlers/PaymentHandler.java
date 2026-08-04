package org.yap.paymentservice.handlers;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.yap.paymentservice.dtos.BalanceDecRequest;
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

    public Mono<ServerResponse> decBalance(ServerRequest request) {
        return request.bodyToMono(BalanceDecRequest.class)
                .flatMap(body -> {
                    Long decVal = body.getDecValue();
                    return service.decBalance(decVal)
                            .flatMap(b -> ServerResponse.ok().bodyValue(b));
                });
    }
}