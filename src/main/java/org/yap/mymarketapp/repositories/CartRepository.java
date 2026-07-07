package org.yap.mymarketapp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.yap.mymarketapp.model.CartModel;

import java.util.List;

@Repository
public interface CartRepository extends JpaRepository<CartModel, Long> {
    List<CartModel> findByItemId(Long itemId);
}
