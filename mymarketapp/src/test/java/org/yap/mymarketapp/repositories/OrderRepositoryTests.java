package org.yap.mymarketapp.repositories;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.yap.mymarketapp.model.ItemModel;
import org.yap.mymarketapp.model.OrderItem;
import org.yap.mymarketapp.model.OrderModel;
import org.yap.mymarketapp.model.UserModel;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class OrderRepositoryTests {

    @DynamicPropertySource
    static void isolatedDatabase(DynamicPropertyRegistry registry) {
        registry.add("spring.r2dbc.url", () -> "r2dbc:h2:mem:///memdb_order_repo;DB_CLOSE_DELAY=-1;DATABASE_TO_UPPER=FALSE;CASE_INSENSITIVE_IDENTIFIERS=TRUE");
    }

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private UserRepository userRepository;

    private ItemModel testItem;
    private OrderModel testOrder;
    private UserModel testUser;

    @BeforeEach
    void setUp() {
        orderItemRepository.deleteAll().block();
        orderRepository.deleteAll().block();
        itemRepository.deleteAll().block();
        userRepository.deleteAll().block();

        testUser = new UserModel("order_test_user", "$2b$10$W16P9J3qtZWprtsYRolqpeIGwN6amvHG1dVaQN452hIoi6sSK/G.a");
        testUser = userRepository.save(testUser).block();

        testItem = new ItemModel();
        testItem.setTitle("Test Item");
        testItem.setDescription("Test Description");
        testItem.setPrice(100L);
        testItem = itemRepository.save(testItem).block();

        testOrder = new OrderModel();
        testOrder.setUserId(testUser.getId());
        testOrder.setTotalSum(100L);
        testOrder = orderRepository.save(testOrder).block();

        OrderItem testOrderItem = new OrderItem(testOrder.getId(), testItem.getId(), 3);
        orderItemRepository.save(testOrderItem).block();
    }

    @Test
    void getItemCountInOrder_ShouldReturnCount_WhenItemExistsInOrder() {
        orderItemRepository.getItemCountInOrder(testOrder.getId(), testItem.getId())
                .as(StepVerifier::create)
                .assertNext(count -> assertThat(count).isEqualTo(3))
                .verifyComplete();
    }

    @Test
    void getItemCountInOrder_ShouldReturnEmpty_WhenItemNotInOrder() {
        orderItemRepository.getItemCountInOrder(testOrder.getId(), 999L)
                .as(StepVerifier::create)
                .verifyComplete();
    }

    @Test
    void getItemCountInOrder_ShouldReturnEmpty_WhenOrderDoesNotExist() {
        orderItemRepository.getItemCountInOrder(999L, testItem.getId())
                .as(StepVerifier::create)
                .verifyComplete();
    }

    @Test
    void findByIdAndUserId_ShouldReturnOrder_WhenOrderBelongsToUser() {
        orderRepository.findByIdAndUserId(testOrder.getId(), testUser.getId())
                .as(StepVerifier::create)
                .assertNext(order -> {
                    assertThat(order.getId()).isEqualTo(testOrder.getId());
                    assertThat(order.getUserId()).isEqualTo(testUser.getId());
                })
                .verifyComplete();
    }

    @Test
    void findByIdAndUserId_ShouldReturnEmpty_WhenOrderBelongsToAnotherUser() {
        UserModel anotherUser = new UserModel("order_test_user_2", "$2b$10$W16P9J3qtZWprtsYRolqpeIGwN6amvHG1dVaQN452hIoi6sSK/G.a");
        anotherUser = userRepository.save(anotherUser).block();

        orderRepository.findByIdAndUserId(testOrder.getId(), anotherUser.getId())
                .as(StepVerifier::create)
                .verifyComplete();
    }
}