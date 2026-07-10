package org.yap.mymarketapp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.yap.mymarketapp.model.CartModel;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<CartModel, Long> {
    Optional<CartModel> findByItemId(@Param("itemId") Long itemId);
}
