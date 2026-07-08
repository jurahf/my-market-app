package org.yap.mymarketapp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.yap.mymarketapp.dtos.CartActionEnum;
import org.yap.mymarketapp.dtos.CartResponse;
import org.yap.mymarketapp.model.CartModel;
import org.yap.mymarketapp.model.ItemModel;
import org.yap.mymarketapp.repositories.CartRepository;
import org.yap.mymarketapp.repositories.ItemRepository;
import org.yap.mymarketapp.services.CartService;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.sql.init.mode=never"
})
@Transactional
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
        cartRepository.deleteAll();
        itemRepository.deleteAll();

        item = new ItemModel();
        item.setTitle("Test Item");
        item.setDescription("Test Description");
        item.setImgPath("/test.jpg");
        item.setPrice(100L);
        item = itemRepository.save(item);
    }

    @Test
    void toCart_ShouldAddItemToCart_WhenNotPresent() {
        // Act
        cartService.toCart(item.getId(), CartActionEnum.PLUS);

        // Assert
        CartModel cart = cartRepository.findByItemId(item.getId()).get(0);
        assertThat(cart).isNotNull();
        assertThat(cart.getItemId()).isEqualTo(item.getId());
        assertThat(cart.getCount()).isEqualTo(1);
    }

    @Test
    void toCart_ShouldIncrementCount_WhenItemInCart() {
        // Arrange
        cartService.toCart(item.getId(), CartActionEnum.PLUS);

        // Act
        cartService.toCart(item.getId(), CartActionEnum.PLUS);

        // Assert
        CartModel cart = cartRepository.findByItemId(item.getId()).get(0);
        assertThat(cart.getCount()).isEqualTo(2);
    }

    @Test
    void toCart_ShouldDecrementCount_WhenItemInCart() {
        // Arrange
        cartService.toCart(item.getId(), CartActionEnum.PLUS);
        cartService.toCart(item.getId(), CartActionEnum.PLUS);

        // Act
        cartService.toCart(item.getId(), CartActionEnum.MINUS);

        // Assert
        CartModel cart = cartRepository.findByItemId(item.getId()).get(0);
        assertThat(cart.getCount()).isEqualTo(1);
    }

    @Test
    void toCart_ShouldRemoveItem_WhenCountBecomesZero() {
        // Arrange
        cartService.toCart(item.getId(), CartActionEnum.PLUS);

        // Act
        cartService.toCart(item.getId(), CartActionEnum.MINUS);

        // Assert
        assertThat(cartRepository.findByItemId(item.getId())).isEmpty();
    }

    @Test
    void toCart_ShouldRemoveItem_WhenActionDelete() {
        // Arrange
        cartService.toCart(item.getId(), CartActionEnum.PLUS);

        // Act
        cartService.toCart(item.getId(), CartActionEnum.DELETE);

        // Assert
        assertThat(cartRepository.findByItemId(item.getId())).isEmpty();
    }

    @Test
    void getItemsInCart_ShouldReturnEmptyResponse_WhenCartEmpty() {
        // Act
        CartResponse response = cartService.getItemsInCart();

        // Assert
        assertThat(response.getItems()).isEmpty();
        assertThat(response.getTotal()).isEqualTo(0);
    }
}
