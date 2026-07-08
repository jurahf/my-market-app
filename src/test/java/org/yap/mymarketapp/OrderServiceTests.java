package org.yap.mymarketapp;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.yap.mymarketapp.dtos.ItemDto;
import org.yap.mymarketapp.dtos.OrderDto;
import org.yap.mymarketapp.model.CartModel;
import org.yap.mymarketapp.model.ItemModel;
import org.yap.mymarketapp.model.OrderItem;
import org.yap.mymarketapp.model.OrderModel;
import org.yap.mymarketapp.repositories.CartRepository;
import org.yap.mymarketapp.repositories.OrderRepository;
import org.yap.mymarketapp.services.OrderService;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTests {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CartRepository cartRepository;

    @InjectMocks
    private OrderService orderService;

    private ItemModel item1;
    private ItemModel item2;
    private OrderModel orderModel;
    private CartModel cart1;
    private CartModel cart2;

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

        cart1 = new CartModel();
        cart1.setId(1L);
        cart1.setItem(item1);
        cart1.setCount(2);

        cart2 = new CartModel();
        cart2.setId(2L);
        cart2.setItem(item2);
        cart2.setCount(1);

        orderModel = new OrderModel();
        orderModel.setId(1L);
        orderModel.setOrderItems(List.of(new OrderItem(orderModel, item1, 1), new OrderItem(orderModel, item2, 1)));
        orderModel.setTotalSum(400L);
    }

    @Test
    void getAll_ShouldReturnListOfOrderDtos() {
        // Arrange
        when(orderRepository.findAll()).thenReturn(List.of(orderModel));
        when(orderRepository.getItemCountInOrder(anyLong(), anyLong()))
                .thenReturn(Optional.of(1));

        // Act
        List<OrderDto> result = orderService.getAll();

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).id()).isEqualTo(1L);
        assertThat(result.get(0).items()).hasSize(2);
        assertThat(result.get(0).totalSum()).isEqualTo(400L);

        verify(orderRepository).findAll();
        verify(orderRepository, times(2)).getItemCountInOrder(anyLong(), anyLong());
    }

    @Test
    void getById_ShouldReturnOrderDto_WhenOrderExists() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.of(orderModel));
        when(orderRepository.getItemCountInOrder(anyLong(), anyLong()))
                .thenReturn(Optional.of(1));

        // Act
        OrderDto result = orderService.getById(1L);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.items()).hasSize(2);
        assertThat(result.totalSum()).isEqualTo(400L);

        verify(orderRepository).findById(1L);
        verify(orderRepository, times(2)).getItemCountInOrder(anyLong(), anyLong());
    }

    @Test
    void getById_ShouldThrowException_WhenOrderNotFound() {
        // Arrange
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> orderService.getById(999L))
                .isInstanceOf(RuntimeException.class);

        verify(orderRepository).findById(999L);
        verify(orderRepository, never()).getItemCountInOrder(anyLong(), anyLong());
    }

    @Test
    void createOrder_ShouldHandleEmptyCart() {
        // Arrange
        when(cartRepository.findAll()).thenReturn(List.of());

        OrderModel emptyOrder = new OrderModel();
        emptyOrder.setId(2L);
        emptyOrder.setOrderItems(List.of());
        emptyOrder.setTotalSum(0L);

        when(orderRepository.save(any(OrderModel.class))).thenReturn(emptyOrder);

        // Act
        Long orderId = orderService.createOrder();

        // Assert
        assertThat(orderId).isEqualTo(2L);

        verify(cartRepository).findAll();
        verify(orderRepository).save(any(OrderModel.class));
        verify(cartRepository).deleteAll();
    }

    @Test
    void createOrder_ShouldCalculateTotalSumCorrectly() {
        // Arrange
        when(cartRepository.findAll()).thenReturn(List.of(cart1, cart2));
        when(orderRepository.save(any(OrderModel.class))).thenReturn(orderModel);

        // Act
        orderService.createOrder();

        // Assert
        verify(orderRepository).save(argThat(order ->
                order.getTotalSum() == 400L // 100*2 + 200*1
        ));
    }
}
