package org.yap.mymarketapp.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.yap.mymarketapp.dtos.CartActionEnum;
import org.yap.mymarketapp.dtos.CartResponse;
import org.yap.mymarketapp.model.CartModel;
import org.yap.mymarketapp.model.ItemModel;
import org.yap.mymarketapp.model.UserModel;
import org.yap.mymarketapp.repositories.CartRepository;
import org.yap.mymarketapp.repositories.ItemRepository;
import org.yap.mymarketapp.repositories.UserRepository;
import org.yap.mymarketapp.security.CurrentUserService;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest
class CartServiceIntegrationTests {

    @DynamicPropertySource
    static void isolatedDatabase(DynamicPropertyRegistry registry) {
        registry.add("spring.r2dbc.url", () -> "r2dbc:h2:mem:///memdb_cart_service;DB_CLOSE_DELAY=-1;DATABASE_TO_UPPER=FALSE;CASE_INSENSITIVE_IDENTIFIERS=TRUE");
    }

    @Autowired
    private CartService cartService;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @MockitoBean
    private CurrentUserService currentUser;

    private ItemModel item;
    private long userId;

    @BeforeEach
    void setUp() {
        cartRepository.deleteAll().block();
        itemRepository.deleteAll().block();
        userRepository.deleteAll().block();

        UserModel user = new UserModel("cart_service_test_user", "$2b$10$W16P9J3qtZWprtsYRolqpeIGwN6amvHG1dVaQN452hIoi6sSK/G.a");
        userId = userRepository.save(user).block().getId();
        when(currentUser.getCurrentUserId()).thenReturn(Mono.just(userId));

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
                .then(cartRepository.findByItemIdAndUserId(item.getId(), userId))
                .as(StepVerifier::create)
                .assertNext(cart -> {
                    assertThat(cart).isNotNull();
                    assertThat(cart.getItemId()).isEqualTo(item.getId());
                    assertThat(cart.getUserId()).isEqualTo(userId);
                    assertThat(cart.getCount()).isEqualTo(1);
                })
                .verifyComplete();
    }

    @Test
    void toCart_ShouldIncrementCount_WhenItemInCart() {
        cartService.toCart(item.getId(), CartActionEnum.PLUS)
                .then(cartService.toCart(item.getId(), CartActionEnum.PLUS))
                .then(cartRepository.findByItemIdAndUserId(item.getId(), userId))
                .as(StepVerifier::create)
                .assertNext(cart -> assertThat(cart.getCount()).isEqualTo(2))
                .verifyComplete();
    }

    @Test
    void toCart_ShouldDecrementCount_WhenItemInCart() {
        cartService.toCart(item.getId(), CartActionEnum.PLUS)
                .then(cartService.toCart(item.getId(), CartActionEnum.PLUS))
                .then(cartService.toCart(item.getId(), CartActionEnum.MINUS))
                .then(cartRepository.findByItemIdAndUserId(item.getId(), userId))
                .as(StepVerifier::create)
                .assertNext(cart -> assertThat(cart.getCount()).isEqualTo(1))
                .verifyComplete();
    }

    @Test
    void toCart_ShouldRemoveItem_WhenCountBecomesZero() {
        cartService.toCart(item.getId(), CartActionEnum.PLUS)
                .then(cartService.toCart(item.getId(), CartActionEnum.MINUS))
                .then(cartRepository.findByItemIdAndUserId(item.getId(), userId))
                .as(StepVerifier::create)
                .verifyComplete();
    }

    @Test
    void toCart_ShouldRemoveItem_WhenActionDelete() {
        cartService.toCart(item.getId(), CartActionEnum.PLUS)
                .then(cartService.toCart(item.getId(), CartActionEnum.DELETE))
                .then(cartRepository.findByItemIdAndUserId(item.getId(), userId))
                .as(StepVerifier::create)
                .verifyComplete();
    }

    @Test
    void toCart_ShouldNotAddItemForAnotherUser() {
        UserModel anotherUser = new UserModel("cart_service_test_user_2", "$2b$10$W16P9J3qtZWprtsYRolqpeIGwN6amvHG1dVaQN452hIoi6sSK/G.a");
        long anotherUserId = userRepository.save(anotherUser).block().getId();

        cartService.toCart(item.getId(), CartActionEnum.PLUS)
                .then(cartRepository.findByItemIdAndUserId(item.getId(), anotherUserId))
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