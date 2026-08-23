package org.yap.mymarketapp.repositories;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.yap.mymarketapp.model.OrderModel;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface OrderRepository extends R2dbcRepository<OrderModel, Long> {
    Flux<OrderModel> findAllByUserId(@Param("userId") Long userId);

    Mono<OrderModel> findByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);
}