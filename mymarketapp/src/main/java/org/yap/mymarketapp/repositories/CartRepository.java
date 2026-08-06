package org.yap.mymarketapp.repositories;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.yap.mymarketapp.model.CartModel;
import reactor.core.publisher.Mono;

@Repository
public interface CartRepository extends R2dbcRepository<CartModel, Long> {
    Mono<CartModel> findByItemId(@Param("itemId") Long itemId);
}
