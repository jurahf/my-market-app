package org.yap.paymentservice.services;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

@Service
public class PaymentService {

    private long startSumStub = 10_000L;

    public Mono<Long> getBalance() {
        return Mono.just(startSumStub);
    }

    public Mono<Long> decBalance(Long decValue) {
        return Mono.just(startSumStub - decValue);
    }
}