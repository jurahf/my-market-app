package org.yap.mymarketapp.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.server.ResponseStatusException;
import org.yap.mymarketapp.dtos.ItemDto;
import org.yap.mymarketapp.dtos.PagingDto;
import org.yap.mymarketapp.dtos.SearchRequest;
import org.yap.mymarketapp.dtos.SearchResponse;
import org.yap.mymarketapp.dtos.SortFieldEnum;
import org.yap.mymarketapp.model.CartModel;
import org.yap.mymarketapp.model.ItemModel;
import org.yap.mymarketapp.repositories.ItemRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceTests {

    @Mock
    private ItemRepository repository;

    @InjectMocks
    private ItemService itemService;

    private ItemModel item1;
    private ItemModel item2;
    private ItemModel item3;

    @BeforeEach
    void setUp() {
        CartModel cart = new CartModel();
        cart.setCount(5);

        item1 = new ItemModel();
        item1.setId(1L);
        item1.setTitle("Apple");
        item1.setDescription("Fresh red apple");
        item1.setPrice(100);
        item1.setImgPath("/images/apple.jpg");
        item1.setCart(cart);

        item2 = new ItemModel();
        item2.setId(2L);
        item2.setTitle("Banana");
        item2.setDescription("Yellow banana");
        item2.setPrice(50);
        item2.setImgPath("/images/banana.jpg");
        item2.setCart(null);

        item3 = new ItemModel();
        item3.setId(3L);
        item3.setTitle("Cherry");
        item3.setDescription("Sweet cherry");
        item3.setPrice(150);
        item3.setImgPath("/images/cherry.jpg");
        item3.setCart(null);
    }

    @Test
    void getById_ShouldReturnItemDto_WhenItemExists() {
        // Arrange
        when(repository.findById(1L)).thenReturn(Optional.of(item1));

        // Act
        ItemDto result = itemService.getById(1L);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.title()).isEqualTo("Apple");
        assertThat(result.description()).isEqualTo("Fresh red apple");
        assertThat(result.price()).isEqualTo(100);
        assertThat(result.imgPath()).isEqualTo("/images/apple.jpg");
        assertThat(result.count()).isEqualTo(5);

        verify(repository).findById(1L);
    }

    @Test
    void getById_ShouldThrowNotFoundException_WhenItemDoesNotExist() {
        // Arrange
        when(repository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> itemService.getById(999L))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("status", org.springframework.http.HttpStatus.NOT_FOUND);

        verify(repository).findById(999L);
    }

    @Test
    void getAll_ShouldReturnAllItems_WhenNoSearchAndDefaultSort() {
        // Arrange
        List<ItemModel> items = List.of(item1, item2, item3);
        Page<ItemModel> page = new PageImpl<>(items);

        when(repository.findAll(any(Pageable.class))).thenReturn(page);

        SearchRequest request = new SearchRequest(null, SortFieldEnum.NO, 1, 5);

        // Act
        SearchResponse response = itemService.getAll(request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.search()).isNull();
        assertThat(response.sort()).isEqualTo(SortFieldEnum.NO);
        assertThat(response.items()).hasSize(1); // 3 items in 1 chunk
        assertThat(response.items().get(0)).hasSize(3);
        assertThat(response.items().get(0).get(0).title()).isEqualTo("Apple");
        assertThat(response.items().get(0).get(1).title()).isEqualTo("Banana");
        assertThat(response.items().get(0).get(2).title()).isEqualTo("Cherry");

        PagingDto paging = response.paging();
        assertThat(paging.pageSize()).isEqualTo(5);
        assertThat(paging.pageNumber()).isEqualTo(1);
        assertThat(paging.hasPrevious()).isFalse();
        assertThat(paging.hasNext()).isFalse();

        verify(repository).findAll(any(Pageable.class));
        verify(repository, never()).findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                anyString(), anyString(), any(Pageable.class));
    }

    @Test
    void getAll_ShouldReturnFilteredItems_WhenSearchProvided() {
        // Arrange
        List<ItemModel> items = List.of(item1, item2);
        Page<ItemModel> page = new PageImpl<>(items);

        when(repository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                eq("ap"), eq("ap"), any(Pageable.class))).thenReturn(page);

        SearchRequest request = new SearchRequest("ap", SortFieldEnum.NO, 1, 5);

        // Act
        SearchResponse response = itemService.getAll(request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.search()).isEqualTo("ap");
        assertThat(response.items()).hasSize(1);
        assertThat(response.items().get(0).stream().filter(x -> x.id() != -1)).hasSize(2);
        assertThat(response.items().get(0).get(0).title()).isEqualTo("Apple");
        assertThat(response.items().get(0).get(1).title()).isEqualTo("Banana");

        verify(repository).findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                eq("ap"), eq("ap"), any(Pageable.class));
        verify(repository, never()).findAll(any(Pageable.class));
    }

    @Test
    void getAll_ShouldPaginateCorrectly() {
        // Arrange
        List<ItemModel> items = List.of(item1, item2, item3);
        Page<ItemModel> page = new PageImpl<>(items);

        when(repository.findAll(any(Pageable.class))).thenReturn(page);

        SearchRequest request = new SearchRequest(null, SortFieldEnum.NO, 2, 2);

        // Act
        SearchResponse response = itemService.getAll(request);

        // Assert
        PagingDto paging = response.paging();
        assertThat(paging.pageNumber()).isEqualTo(2);
        assertThat(paging.pageSize()).isEqualTo(2);

        // Verify pageable was created with correct parameters
        verify(repository).findAll(any(Pageable.class));
    }

    @Test
    void getAll_ShouldHandleInvalidPageNumber() {
        // Arrange
        List<ItemModel> items = List.of(item1);
        Page<ItemModel> page = new PageImpl<>(items);

        when(repository.findAll(any(Pageable.class))).thenReturn(page);

        SearchRequest request = new SearchRequest(null, SortFieldEnum.NO, 0, 5);

        // Act
        SearchResponse response = itemService.getAll(request);

        // Assert
        assertThat(response.paging().pageNumber()).isEqualTo(0);
        verify(repository).findAll(any(Pageable.class));
    }

    @Test
    void getAll_ShouldHandleInvalidPageSize() {
        // Arrange
        List<ItemModel> items = List.of(item1);
        Page<ItemModel> page = new PageImpl<>(items);

        when(repository.findAll(any(Pageable.class))).thenReturn(page);

        SearchRequest request = new SearchRequest(null, SortFieldEnum.NO, 1, -5);

        // Act
        SearchResponse response = itemService.getAll(request);

        // Assert
        assertThat(response.paging().pageSize()).isEqualTo(-5);
        verify(repository).findAll(any(Pageable.class));
    }

    @Test
    void getAll_ShouldSortByAlphabeticalOrder() {
        // Arrange
        List<ItemModel> items = List.of(item1, item2, item3);
        Page<ItemModel> page = new PageImpl<>(items);

        when(repository.findAll(any(Pageable.class))).thenReturn(page);

        SearchRequest request = new SearchRequest(null, SortFieldEnum.ALPHA, 1, 10);

        // Act
        SearchResponse response = itemService.getAll(request);

        // Assert
        assertThat(response.sort()).isEqualTo(SortFieldEnum.ALPHA);
        assertThat(response.items().get(0).get(0).title()).isEqualTo("Apple");
        assertThat(response.items().get(0).get(1).title()).isEqualTo("Banana");
        assertThat(response.items().get(0).get(2).title()).isEqualTo("Cherry");

        verify(repository).findAll(any(Pageable.class));
    }

    @Test
    void getAll_ShouldSortByPrice() {
        // Arrange
        List<ItemModel> items = List.of(item2, item1, item3); // Banana(50), Apple(100), Cherry(150)
        Page<ItemModel> page = new PageImpl<>(items);

        when(repository.findAll(any(Pageable.class))).thenReturn(page);

        SearchRequest request = new SearchRequest(null, SortFieldEnum.PRICE, 1, 10);

        // Act
        SearchResponse response = itemService.getAll(request);

        // Assert
        assertThat(response.sort()).isEqualTo(SortFieldEnum.PRICE);
        assertThat(response.items().get(0).get(0).title()).isEqualTo("Banana");
        assertThat(response.items().get(0).get(1).title()).isEqualTo("Apple");
        assertThat(response.items().get(0).get(2).title()).isEqualTo("Cherry");

        verify(repository).findAll(any(Pageable.class));
    }

    @Test
    void getAll_ShouldHaveHasNextAndHasPreviousFlags() {
        // Arrange
        List<ItemModel> items = List.of(item1, item2);
        Page<ItemModel> page = new PageImpl<>(items, PageRequest.of(1, 2), 10);

        when(repository.findAll(any(Pageable.class))).thenReturn(page);

        SearchRequest request = new SearchRequest(null, SortFieldEnum.NO, 2, 2);

        // Act
        SearchResponse response = itemService.getAll(request);

        // Assert
        assertThat(response.paging().hasPrevious()).isTrue();
        assertThat(response.paging().hasNext()).isTrue();
    }

    @Test
    void getAll_ShouldFillChunksWithEmptyItems() {
        // Arrange
        List<ItemModel> items = List.of(item1, item2); // Only 2 items
        Page<ItemModel> page = new PageImpl<>(items);

        when(repository.findAll(any(Pageable.class))).thenReturn(page);

        SearchRequest request = new SearchRequest(null, SortFieldEnum.NO, 1, 5);

        // Act
        SearchResponse response = itemService.getAll(request);

        // Assert
        assertThat(response.items().get(0)).hasSize(3);
        assertThat(response.items().get(0).get(0).id()).isEqualTo(1L);
        assertThat(response.items().get(0).get(1).id()).isEqualTo(2L);
        assertThat(response.items().get(0).get(2).id()).isEqualTo(-1); // Placeholder
        assertThat(response.items().get(0).get(2).title()).isEmpty();
    }
}
