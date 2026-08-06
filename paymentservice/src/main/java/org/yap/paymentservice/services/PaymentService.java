package org.yap.paymentservice.services;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicLong;

@Service
public class PaymentService {

    private AtomicLong startSumStub = new AtomicLong(10_000L);

    public Mono<Long> getBalance() {
        return Mono.just(startSumStub.get());
    }

    public Mono<Long> decBalance(Long decValue) {
        return Mono.just(startSumStub.addAndGet(-decValue));
    }
}