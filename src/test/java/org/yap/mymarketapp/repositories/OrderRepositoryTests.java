package org.yap.mymarketapp.repositories;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.yap.mymarketapp.model.ItemModel;
import org.yap.mymarketapp.model.OrderItem;
import org.yap.mymarketapp.model.OrderModel;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class OrderRepositoryTests {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ItemRepository itemRepository;

    private ItemModel testItem;
    private OrderModel testOrder;
    private OrderItem testOrderItem;

    @BeforeEach
    void setUp() {
        // Создаем тестовый товар
        testItem = new ItemModel();
        testItem.setTitle("Test Item");
        testItem.setDescription("Test Description");
        testItem.setPrice(100L);
        testItem = itemRepository.save(testItem);

        testOrder = new OrderModel();

        // Создаем тестовый элемент заказа
        testOrderItem = new OrderItem();
        testOrderItem.setOrder(testOrder);
        testOrderItem.setItem(testItem);
        testOrderItem.setCount(3);

        // Создаем тестовый заказ
        testOrder.setTotalSum(100L);
        testOrder.setOrderItems(List.of(testOrderItem));
        testOrder = orderRepository.save(testOrder);
    }

    @Test
    void getItemCountInOrder_ShouldReturnCount_WhenItemExistsInOrder() {
        // When
        Optional<Integer> count = orderRepository.getItemCountInOrder(testItem.getId(), testOrder.getId());

        // Then
        assertThat(count).isPresent();
        assertThat(count.get()).isEqualTo(3);
    }

    @Test
    void getItemCountInOrder_ShouldReturnEmpty_WhenItemNotInOrder() {
        // When
        Optional<Integer> count = orderRepository.getItemCountInOrder(999L, testOrder.getId());

        // Then
        assertThat(count).isEmpty();
    }

    @Test
    void getItemCountInOrder_ShouldReturnEmpty_WhenOrderDoesNotExist() {
        // When
        Optional<Integer> count = orderRepository.getItemCountInOrder(testItem.getId(), 999L);

        // Then
        assertThat(count).isEmpty();
    }
}
