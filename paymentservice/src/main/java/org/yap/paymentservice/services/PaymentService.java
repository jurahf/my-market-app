package org.yap.paymentservice.services;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class PaymentService {

    public Mono<Long> getBalance() {
        return Mono.just(500L);
    }

}