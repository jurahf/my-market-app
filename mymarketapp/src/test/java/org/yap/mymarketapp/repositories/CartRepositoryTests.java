package org.yap.mymarketapp.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.yap.mymarketapp.model.CartModel;
import org.yap.mymarketapp.model.ItemModel;
import org.yap.mymarketapp.model.UserModel;
import org.yap.mymarketapp.repositories.CartRepository;
import org.yap.mymarketapp.repositories.ItemRepository;
import org.yap.mymarketapp.repositories.UserRepository;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CartRepositoryTests {

    @DynamicPropertySource
    static void isolatedDatabase(DynamicPropertyRegistry registry) {
        registry.add("spring.r2dbc.url", () -> "r2dbc:h2:mem:///memdb_cart_repo;DB_CLOSE_DELAY=-1;DATABASE_TO_UPPER=FALSE;CASE_INSENSITIVE_IDENTIFIERS=TRUE");
    }

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    private ItemModel testItem;
    private CartModel testCart;
    private UserModel testUser;

    @BeforeEach
    void setUp() {
        cartRepository.deleteAll().block();
        itemRepository.deleteAll().block();
        userRepository.deleteAll().block();

        testUser = new UserModel("cart_test_user", "$2b$10$W16P9J3qtZWprtsYRolqpeIGwN6amvHG1dVaQN452hIoi6sSK/G.a");
        testUser = userRepository.save(testUser).block();

        testItem = new ItemModel();
        testItem.setTitle("Test Item");
        testItem.setDescription("Test Description");
        testItem.setPrice(100L);
        testItem = itemRepository.save(testItem).block();

        testCart = new CartModel();
        testCart.setUserId(testUser.getId());
        testCart.setItemId(testItem.getId());
        testCart.setCount(2);
        testCart = cartRepository.save(testCart).block();
    }

    @Test
    void findByItemIdAndUserId_ShouldReturnCart_WhenItemExists() {
        cartRepository.findByItemIdAndUserId(testItem.getId(), testUser.getId())
                .as(StepVerifier::create)
                .assertNext(cart -> {
                    assertThat(cart.getItemId()).isEqualTo(testItem.getId());
                    assertThat(cart.getUserId()).isEqualTo(testUser.getId());
                    assertThat(cart.getCount()).isEqualTo(2);
                })
                .verifyComplete();
    }

    @Test
    void findByItemIdAndUserId_ShouldReturnEmpty_WhenItemDoesNotExist() {
        cartRepository.findByItemIdAndUserId(999L, testUser.getId())
                .as(StepVerifier::create)
                .verifyComplete();
    }

    @Test
    void findByItemIdAndUserId_ShouldReturnEmpty_WhenItemBelongsToAnotherUser() {
        UserModel anotherUser = new UserModel("cart_test_user_2", "$2b$10$W16P9J3qtZWprtsYRolqpeIGwN6amvHG1dVaQN452hIoi6sSK/G.a");
        anotherUser = userRepository.save(anotherUser).block();

        cartRepository.findByItemIdAndUserId(testItem.getId(), anotherUser.getId())
                .as(StepVerifier::create)
                .verifyComplete();
    }
}