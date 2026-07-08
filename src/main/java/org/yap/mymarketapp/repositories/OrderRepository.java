package org.yap.mymarketapp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.yap.mymarketapp.model.OrderModel;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<OrderModel, Long> {

    @Query("SELECT oi.count FROM OrderItem oi WHERE oi.order.id = :orderId AND oi.item.id = :itemId")
    Optional<Integer> getItemCountInOrder(long itemId, long orderId);
}
