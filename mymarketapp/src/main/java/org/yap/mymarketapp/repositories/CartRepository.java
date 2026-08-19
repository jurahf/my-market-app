package org.yap.mymarketapp.repositories;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.yap.mymarketapp.model.CartModel;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface CartRepository extends R2dbcRepository<CartModel, Long> {
    Mono<CartModel> findByItemIdAndUserId(@Param("itemId") Long itemId, @Param("userId") Long userId);

    Flux<CartModel> findAllByUserId(@Param("userId") Long userId);

    @Query("DELETE FROM cart WHERE user_id = :userId")
    Mono<Void> deleteAllByUserId(@Param("userId") Long userId);
}