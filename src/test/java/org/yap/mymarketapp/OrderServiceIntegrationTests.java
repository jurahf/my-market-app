package org.yap.mymarketapp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.yap.mymarketapp.dtos.OrderDto;
import org.yap.mymarketapp.model.CartModel;
import org.yap.mymarketapp.model.ItemModel;
import org.yap.mymarketapp.repositories.CartRepository;
import org.yap.mymarketapp.repositories.ItemRepository;
import org.yap.mymarketapp.repositories.OrderRepository;
import org.yap.mymarketapp.services.OrderService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.sql.init.mode=never"
})
@Transactional
class OrderServiceIntegrationTests {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ItemRepository itemRepository;

    private ItemModel item1;
    private ItemModel item2;

    @BeforeEach
    void setUp() {
        cartRepository.deleteAll();
        orderRepository.deleteAll();
        itemRepository.deleteAll();

        item1 = new ItemModel();
        item1.setTitle("Integration Item 1");
        item1.setDescription("Description 1");
        item1.setPrice(100L);
        item1.setImgPath("/int1.jpg");
        item1 = itemRepository.save(item1);

        item2 = new ItemModel();
        item2.setTitle("Integration Item 2");
        item2.setDescription("Description 2");
        item2.setPrice(200L);
        item2.setImgPath("/int2.jpg");
        item2 = itemRepository.save(item2);

        // Add items to cart
        CartModel cart1 = new CartModel();
        cart1.setItem(item1);
        cart1.setCount(2);
        cartRepository.save(cart1);

        CartModel cart2 = new CartModel();
        cart2.setItem(item2);
        cart2.setCount(1);
        cartRepository.save(cart2);
    }

    @Test
    void createOrder_IntegrationTest() {
        // Act
        Long orderId = orderService.createOrder();

        // Assert
        assertThat(orderId).isNotNull();

        // Verify order was created
        var orderOpt = orderRepository.findById(orderId);
        assertThat(orderOpt).isPresent();

        OrderDto order = orderService.getById(orderId);
        assertThat(order.items()).hasSize(2);
        assertThat(order.totalSum()).isEqualTo(400L); // 100*2 + 200*1

        // Verify cart is empty
        assertThat(cartRepository.findAll()).isEmpty();
    }

    @Test
    void getAll_IntegrationTest() {
        // Arrange
        Long orderId = orderService.createOrder();

        // Act
        List<OrderDto> orders = orderService.getAll();

        // Assert
        assertThat(orders).hasSize(1);
        assertThat(orders.get(0).id()).isEqualTo(orderId);
        assertThat(orders.get(0).items()).hasSize(2);
    }
}