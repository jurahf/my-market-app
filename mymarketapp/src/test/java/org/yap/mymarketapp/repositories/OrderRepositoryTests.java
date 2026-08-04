package org.yap.mymarketapp.repositories;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.yap.mymarketapp.model.ItemModel;
import org.yap.mymarketapp.model.OrderItem;
import org.yap.mymarketapp.model.OrderModel;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class OrderRepositoryTests {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    private ItemModel testItem;
    private OrderModel testOrder;

    @BeforeEach
    void setUp() {
        orderItemRepository.deleteAll().block();
        orderRepository.deleteAll().block();
        itemRepository.deleteAll().block();

        testItem = new ItemModel();
        testItem.setTitle("Test Item");
        testItem.setDescription("Test Description");
        testItem.setPrice(100L);
        testItem = itemRepository.save(testItem).block();

        testOrder = new OrderModel();
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
}
