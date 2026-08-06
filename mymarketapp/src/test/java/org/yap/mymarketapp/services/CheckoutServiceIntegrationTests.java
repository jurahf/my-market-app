package org.yap.mymarketapp.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.server.ResponseStatusException;
import org.yap.mymarketapp.dtos.OrderDto;
import org.yap.mymarketapp.model.CartModel;
import org.yap.mymarketapp.model.ItemModel;
import org.yap.mymarketapp.repositories.CartRepository;
import org.yap.mymarketapp.repositories.ItemRepository;
import org.yap.mymarketapp.repositories.OrderRepository;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CheckoutServiceIntegrationTests {

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

    private ItemModel item1;
    private ItemModel item2;

    @BeforeEach
    void setUp() {
        cartRepository.deleteAll().block();
        orderRepository.deleteAll().block();
        itemRepository.deleteAll().block();

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

        CartModel cart1 = new CartModel(item1.getId(), 2);
        cartRepository.save(cart1).block();

        CartModel cart2 = new CartModel(item2.getId(), 1);
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
                .flatMap(orderId -> cartRepository.findAll().collectList()
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

}
