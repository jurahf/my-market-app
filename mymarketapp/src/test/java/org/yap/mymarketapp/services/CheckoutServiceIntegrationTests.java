package org.yap.mymarketapp.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.server.ResponseStatusException;
import org.yap.mymarketapp.dtos.OrderDto;
import org.yap.mymarketapp.model.CartModel;
import org.yap.mymarketapp.model.ItemModel;
import org.yap.mymarketapp.model.UserModel;
import org.yap.mymarketapp.repositories.CartRepository;
import org.yap.mymarketapp.repositories.ItemRepository;
import org.yap.mymarketapp.repositories.OrderRepository;
import org.yap.mymarketapp.repositories.UserRepository;
import org.yap.mymarketapp.security.CurrentUserService;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest
class CheckoutServiceIntegrationTests {

    @DynamicPropertySource
    static void isolatedDatabase(DynamicPropertyRegistry registry) {
        registry.add("spring.r2dbc.url", () -> "r2dbc:h2:mem:///memdb_checkout_service;DB_CLOSE_DELAY=-1;DATABASE_TO_UPPER=FALSE;CASE_INSENSITIVE_IDENTIFIERS=TRUE");
    }

    @Autowired
    private CheckoutService checkoutService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @MockitoBean
    private CurrentUserService currentUser;

    private ItemModel item1;
    private ItemModel item2;
    private long userId;

    @BeforeEach
    void setUp() {
        cartRepository.deleteAll().block();
        orderRepository.deleteAll().block();
        itemRepository.deleteAll().block();
        userRepository.deleteAll().block();

        UserModel user = new UserModel("checkout_service_test_user", "$2b$10$W16P9J3qtZWprtsYRolqpeIGwN6amvHG1dVaQN452hIoi6sSK/G.a");
        userId = userRepository.save(user).block().getId();
        when(currentUser.getCurrentUserId()).thenReturn(Mono.just(userId));

        item1 = new ItemModel();
        item1.setTitle("Integration Item 1");
        item1.setDescription("Description 1");
        item1.setPrice(100L);
        item1.setImgPath("/int1.jpg");
        item1 = itemRepository.save(item1).block();

        item2 = new ItemModel();
        item2.setTitle("Integration Item 2");
        item2.setDescription("Description 2");
        item2.setPrice(200L);
        item2.setImgPath("/int2.jpg");
        item2 = itemRepository.save(item2).block();

        CartModel cart1 = new CartModel(userId, item1.getId(), 2);
        cartRepository.save(cart1).block();

        CartModel cart2 = new CartModel(userId, item2.getId(), 1);
        cartRepository.save(cart2).block();
    }

    @Test
    void createOrder_IntegrationTest() {
        checkoutService.createOrder()
                .flatMap(orderId -> orderService.getById(orderId).map(order -> {
                    assertThat(orderId).isNotNull();
                    assertThat(order.items()).hasSize(2);
                    assertThat(order.totalSum()).isEqualTo(400L);
                    return orderId;
                }))
                .flatMap(orderId -> cartRepository.findAllByUserId(userId).collectList()
                        .map(carts -> {
                            assertThat(carts).isEmpty();
                            return orderId;
                        }))
                .as(StepVerifier::create)
                .assertNext(orderId -> assertThat(orderId).isNotNull())
                .verifyComplete();
    }

    @Test
    void getAll_IntegrationTest() {
        checkoutService.createOrder()
                .then(orderService.getAll().collectList())
                .as(StepVerifier::create)
                .assertNext(orders -> {
                    assertThat(orders).hasSize(1);
                    assertThat(orders.get(0).items()).hasSize(2);
                })
                .verifyComplete();
    }

    @Test
    void createOrder_ShouldHandleEmptyCart() {
        cartRepository.deleteAll().block();

        checkoutService.createOrder()
                .as(StepVerifier::create)
                .expectError(ResponseStatusException.class)
                .verify();
    }

    @Test
    void createOrder_ShouldOnlyUseCurrentUsersCart() {
        UserModel anotherUser = new UserModel("checkout_service_test_user_2", "$2b$10$W16P9J3qtZWprtsYRolqpeIGwN6amvHG1dVaQN452hIoi6sSK/G.a");
        long anotherUserId = userRepository.save(anotherUser).block().getId();

        CartModel otherUserCart = new CartModel(anotherUserId, item1.getId(), 5);
        cartRepository.save(otherUserCart).block();

        checkoutService.createOrder()
                .flatMap(orderId -> orderService.getById(orderId).map(order -> {
                    assertThat(order.items()).hasSize(2);
                    assertThat(order.totalSum()).isEqualTo(400L);
                    return orderId;
                }))
                .as(StepVerifier::create)
                .assertNext(orderId -> assertThat(orderId).isNotNull())
                .verifyComplete();
    }

}