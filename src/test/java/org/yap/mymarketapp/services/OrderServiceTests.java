package org.yap.mymarketapp.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.yap.mymarketapp.dtos.OrderDto;
import org.yap.mymarketapp.model.ItemModel;
import org.yap.mymarketapp.model.OrderItem;
import org.yap.mymarketapp.model.OrderModel;
import org.yap.mymarketapp.repositories.ItemRepository;
import org.yap.mymarketapp.repositories.OrderItemRepository;
import org.yap.mymarketapp.repositories.OrderRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTests {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private OrderService orderService;

    private ItemModel item1;
    private ItemModel item2;
    private OrderModel orderModel;

    @BeforeEach
    void setUp() {
        item1 = new ItemModel();
        item1.setId(1L);
        item1.setTitle("Item 1");
        item1.setDescription("Description 1");
        item1.setPrice(100L);
        item1.setImgPath("/img1.jpg");

        item2 = new ItemModel();
        item2.setId(2L);
        item2.setTitle("Item 2");
        item2.setDescription("Description 2");
        item2.setPrice(200L);
        item2.setImgPath("/img2.jpg");

        orderModel = new OrderModel();
        orderModel.setId(1L);
        orderModel.setTotalSum(400L);
    }

    @Test
    void getAll_ShouldReturnListOfOrderDtos() {
        OrderItem oi1 = new OrderItem(1L, 1L, 1);
        OrderItem oi2 = new OrderItem(1L, 2L, 1);

        when(orderRepository.findAll()).thenReturn(Flux.just(orderModel));
        when(orderItemRepository.findByOrderId(1L)).thenReturn(Flux.just(oi1, oi2));
        when(itemRepository.findById(1L)).thenReturn(Mono.just(item1));
        when(itemRepository.findById(2L)).thenReturn(Mono.just(item2));

        orderService.getAll()
                .collectList()
                .as(StepVerifier::create)
                .assertNext(result -> {
                    assertThat(result).hasSize(1);
                    assertThat(result.get(0).id()).isEqualTo(1L);
                    assertThat(result.get(0).items()).hasSize(2);
                    assertThat(result.get(0).totalSum()).isEqualTo(400L);
                })
                .verifyComplete();

        verify(orderRepository).findAll();
        verify(orderItemRepository, times(1)).findByOrderId(anyLong());
    }

    @Test
    void getById_ShouldReturnOrderDto_WhenOrderExists() {
        OrderItem oi1 = new OrderItem(1L, 1L, 1);
        OrderItem oi2 = new OrderItem(1L, 2L, 1);

        when(orderRepository.findById(1L)).thenReturn(Mono.just(orderModel));
        when(orderItemRepository.findByOrderId(1L)).thenReturn(Flux.just(oi1, oi2));
        when(itemRepository.findById(1L)).thenReturn(Mono.just(item1));
        when(itemRepository.findById(2L)).thenReturn(Mono.just(item2));

        orderService.getById(1L)
                .as(StepVerifier::create)
                .assertNext(result -> {
                    assertThat(result).isNotNull();
                    assertThat(result.id()).isEqualTo(1L);
                    assertThat(result.items()).hasSize(2);
                    assertThat(result.totalSum()).isEqualTo(400L);
                })
                .verifyComplete();

        verify(orderRepository).findById(1L);
    }

    @Test
    void getById_ShouldThrowException_WhenOrderNotFound() {
        when(orderRepository.findById(999L)).thenReturn(Mono.empty());

        orderService.getById(999L)
                .as(StepVerifier::create)
                .expectError(RuntimeException.class)
                .verify();

        verify(orderRepository).findById(999L);
        verify(orderItemRepository, never()).findByOrderId(anyLong());
    }
}
