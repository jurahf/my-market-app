package org.yap.mymarketapp.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.yap.mymarketapp.model.CartModel;
import org.yap.mymarketapp.model.ItemModel;


import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CartRepositoryTests {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ItemRepository itemRepository;

    private ItemModel testItem;
    private CartModel testCart;

    @BeforeEach
    void setUp() {
        // Создаем тестовый товар
        testItem = new ItemModel();
        testItem.setTitle("Test Item");
        testItem.setDescription("Test Description");
        testItem.setPrice(100L);
        testItem = itemRepository.save(testItem);

        // Создаем тестовую корзину
        testCart = new CartModel();
        testCart.setItem(testItem);
        testCart.setCount(2);
        testCart = cartRepository.save(testCart);
    }

    @Test
    void findByItemId_ShouldReturnCart_WhenItemExists() {
        // When
        Optional<CartModel> foundCart = cartRepository.findByItemId(testItem.getId());

        // Then
        assertThat(foundCart).isPresent();
        assertThat(foundCart.get().getItem().getId()).isEqualTo(testItem.getId());
        assertThat(foundCart.get().getCount()).isEqualTo(2);
    }

    @Test
    void findByItemId_ShouldReturnEmpty_WhenItemDoesNotExist() {
        // When
        Optional<CartModel> foundCart = cartRepository.findByItemId(999L);

        // Then
        assertThat(foundCart).isEmpty();
    }
}