package org.yap.mymarketapp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.yap.mymarketapp.dtos.CartActionEnum;
import org.yap.mymarketapp.model.CartModel;
import org.yap.mymarketapp.model.ItemModel;
import org.yap.mymarketapp.repositories.CartRepository;
import org.yap.mymarketapp.services.CartService;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class CartServiceTests {

    @Mock
    private CartRepository repository;

    @InjectMocks
    private CartService cartService;

    private ItemModel item;
    private CartModel cart;

    @BeforeEach
    void setUp() {
        item = new ItemModel();
        item.setId(1L);
        item.setTitle("Test Item");
        item.setDescription("Test Description");
        item.setImgPath("/test.jpg");
        item.setPrice(100L);

        cart = new CartModel();
        cart.setId(1L);
        cart.setItemId(1L);
        cart.setCount(1);
        cart.setItem(item);
    }

    @Test
    void toCart_ShouldAddNewItem_WhenItemNotInCartAndActionPlus() {
        // Arrange
        when(repository.findByItemId(1L)).thenReturn(List.of());

        // Act
        cartService.toCart(1L, CartActionEnum.PLUS);

        // Assert
        verify(repository).save(any(CartModel.class));
        verify(repository, never()).deleteById(any());
    }

    @Test
    void toCart_ShouldNotAddItem_WhenItemNotInCartAndActionMinus() {
        // Arrange
        when(repository.findByItemId(1L)).thenReturn(List.of());

        // Act
        cartService.toCart(1L, CartActionEnum.MINUS);

        // Assert
        verify(repository, never()).save(any(CartModel.class));
        verify(repository, never()).deleteById(any());
    }

    @Test
    void toCart_ShouldIncrementCount_WhenItemInCartAndActionPlus() {
        // Arrange
        when(repository.findByItemId(1L)).thenReturn(List.of(cart));

        // Act
        cartService.toCart(1L, CartActionEnum.PLUS);

        // Assert
        assertThat(cart.getCount()).isEqualTo(2);
        verify(repository).save(cart);
        verify(repository, never()).deleteById(any());
    }

    @Test
    void toCart_ShouldDecrementCount_WhenItemInCartAndActionMinus() {
        // Arrange
        cart.setCount(2);
        when(repository.findByItemId(1L)).thenReturn(List.of(cart));

        // Act
        cartService.toCart(1L, CartActionEnum.MINUS);

        // Assert
        assertThat(cart.getCount()).isEqualTo(1);
        verify(repository).save(cart);
        verify(repository, never()).deleteById(any());
    }

    @Test
    void toCart_ShouldDeleteItem_WhenCountBecomesZero() {
        // Arrange
        cart.setCount(1);
        when(repository.findByItemId(1L)).thenReturn(List.of(cart));

        // Act
        cartService.toCart(1L, CartActionEnum.MINUS);

        // Assert
        assertThat(cart.getCount()).isEqualTo(0);
        verify(repository).deleteById(cart.getId());
        verify(repository, never()).save(cart);
    }

    @Test
    void toCart_ShouldDeleteItem_WhenActionDelete() {
        // Arrange
        when(repository.findByItemId(1L)).thenReturn(List.of(cart));

        // Act
        cartService.toCart(1L, CartActionEnum.DELETE);

        // Assert
        assertThat(cart.getCount()).isEqualTo(0);
        verify(repository).deleteById(cart.getId());
        verify(repository, never()).save(cart);
    }

}
