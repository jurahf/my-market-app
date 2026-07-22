package org.yap.mymarketapp.repositories;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import org.yap.mymarketapp.model.OrderModel;
import reactor.core.publisher.Mono;

@Repository
public interface OrderRepository extends R2dbcRepository<OrderModel, Long> {
}
