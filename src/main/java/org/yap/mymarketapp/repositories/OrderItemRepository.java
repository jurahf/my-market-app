package org.yap.mymarketapp.repositories;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.yap.mymarketapp.model.OrderItem;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface OrderItemRepository extends R2dbcRepository<OrderItem, Long> {

    Flux<OrderItem> findByOrderId(@Param("orderId") long orderId);

    @Query("SELECT count FROM order_items WHERE order_id = :orderId AND item_id = :itemId")
    Mono<Integer> getItemCountInOrder(@Param("orderId") long orderId, @Param("itemId") long itemId);
}
