package org.yap.mymarketapp.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.yap.mymarketapp.dtos.CartActionEnum;
import org.yap.mymarketapp.dtos.CartResponse;
import org.yap.mymarketapp.model.CartModel;
import org.yap.mymarketapp.model.ItemModel;
import org.yap.mymarketapp.repositories.CartRepository;
import org.yap.mymarketapp.repositories.ItemRepository;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CartServiceIntegrationTests {

    @Autowired
    private CartService cartService;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ItemRepository itemRepository;

    private ItemModel item;

    @BeforeEach
    void setUp() {
        cartRepository.deleteAll().block();
        itemRepository.deleteAll().block();

        item = new ItemModel();
        item.setTitle("Test Item");
        item.setDescription("Test Description");
        item.setImgPath("/test.jpg");
        item.setPrice(100L);
        item = itemRepository.save(item).block();
    }

    @Test
    void toCart_ShouldAddItemToCart_WhenNotPresent() {
        cartService.toCart(item.getId(), CartActionEnum.PLUS)
                .then(cartRepository.findByItemId(item.getId()))
                .as(StepVerifier::create)
                .assertNext(cart -> {
                    assertThat(cart).isNotNull();
                    assertThat(cart.getItemId()).isEqualTo(item.getId());
                    assertThat(cart.getCount()).isEqualTo(1);
                })
                .verifyComplete();
    }

    @Test
    void toCart_ShouldIncrementCount_WhenItemInCart() {
        cartService.toCart(item.getId(), CartActionEnum.PLUS)
                .then(cartService.toCart(item.getId(), CartActionEnum.PLUS))
                .then(cartRepository.findByItemId(item.getId()))
                .as(StepVerifier::create)
                .assertNext(cart -> assertThat(cart.getCount()).isEqualTo(2))
                .verifyComplete();
    }

    @Test
    void toCart_ShouldDecrementCount_WhenItemInCart() {
        cartService.toCart(item.getId(), CartActionEnum.PLUS)
                .then(cartService.toCart(item.getId(), CartActionEnum.PLUS))
                .then(cartService.toCart(item.getId(), CartActionEnum.MINUS))
                .then(cartRepository.findByItemId(item.getId()))
                .as(StepVerifier::create)
                .assertNext(cart -> assertThat(cart.getCount()).isEqualTo(1))
                .verifyComplete();
    }

    @Test
    void toCart_ShouldRemoveItem_WhenCountBecomesZero() {
        cartService.toCart(item.getId(), CartActionEnum.PLUS)
                .then(cartService.toCart(item.getId(), CartActionEnum.MINUS))
                .then(cartRepository.findByItemId(item.getId()))
                .as(StepVerifier::create)
                .verifyComplete();
    }

    @Test
    void toCart_ShouldRemoveItem_WhenActionDelete() {
        cartService.toCart(item.getId(), CartActionEnum.PLUS)
                .then(cartService.toCart(item.getId(), CartActionEnum.DELETE))
                .then(cartRepository.findByItemId(item.getId()))
                .as(StepVerifier::create)
                .verifyComplete();
    }

    @Test
    void getItemsInCart_ShouldReturnEmptyResponse_WhenCartEmpty() {
        cartService.getItemsInCart()
                .as(StepVerifier::create)
                .assertNext(response -> {
                    assertThat(response.getItems()).isEmpty();
                    assertThat(response.getTotal()).isEqualTo(0);
                })
                .verifyComplete();
    }
}
