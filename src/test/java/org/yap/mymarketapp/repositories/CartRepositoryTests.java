package org.yap.mymarketapp.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.yap.mymarketapp.model.CartModel;
import org.yap.mymarketapp.model.ItemModel;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CartRepositoryTests {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ItemRepository itemRepository;

    private ItemModel testItem;
    private CartModel testCart;

    @BeforeEach
    void setUp() {
        cartRepository.deleteAll().block();
        itemRepository.deleteAll().block();

        testItem = new ItemModel();
        testItem.setTitle("Test Item");
        testItem.setDescription("Test Description");
        testItem.setPrice(100L);
        testItem = itemRepository.save(testItem).block();

        testCart = new CartModel();
        testCart.setItemId(testItem.getId());
        testCart.setCount(2);
        testCart = cartRepository.save(testCart).block();
    }

    @Test
    void findByItemId_ShouldReturnCart_WhenItemExists() {
        cartRepository.findByItemId(testItem.getId())
                .as(StepVerifier::create)
                .assertNext(cart -> {
                    assertThat(cart.getItemId()).isEqualTo(testItem.getId());
                    assertThat(cart.getCount()).isEqualTo(2);
                })
                .verifyComplete();
    }

    @Test
    void findByItemId_ShouldReturnEmpty_WhenItemDoesNotExist() {
        cartRepository.findByItemId(999L)
                .as(StepVerifier::create)
                .verifyComplete();
    }
}
