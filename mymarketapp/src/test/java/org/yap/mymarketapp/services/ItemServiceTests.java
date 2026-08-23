package org.yap.mymarketapp.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import org.yap.mymarketapp.dtos.ItemDto;
import org.yap.mymarketapp.dtos.PagingDto;
import org.yap.mymarketapp.dtos.SearchRequest;
import org.yap.mymarketapp.dtos.SearchResponse;
import org.yap.mymarketapp.dtos.SortFieldEnum;
import org.yap.mymarketapp.model.CartModel;
import org.yap.mymarketapp.model.ItemModel;
import org.yap.mymarketapp.repositories.CartRepository;
import org.yap.mymarketapp.repositories.ItemRepository;
import org.yap.mymarketapp.security.CurrentUserService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceTests {

    @Mock
    private ItemRepository repository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CurrentUserService currentUser;

    @InjectMocks
    private ItemService itemService;

    private ItemModel item1;
    private ItemModel item2;
    private ItemModel item3;

    @BeforeEach
    void setUp() {
        lenient().when(currentUser.getCurrentUserId()).thenReturn(Mono.just(1L));

        item1 = new ItemModel();
        item1.setId(1L);
        item1.setTitle("Apple");
        item1.setDescription("Fresh red apple");
        item1.setPrice(100);
        item1.setImgPath("/images/apple.jpg");

        item2 = new ItemModel();
        item2.setId(2L);
        item2.setTitle("Banana");
        item2.setDescription("Yellow banana");
        item2.setPrice(50);
        item2.setImgPath("/images/banana.jpg");

        item3 = new ItemModel();
        item3.setId(3L);
        item3.setTitle("Cherry");
        item3.setDescription("Sweet cherry");
        item3.setPrice(150);
        item3.setImgPath("/images/cherry.jpg");
    }

    @Test
    void getById_ShouldReturnItemDto_WhenItemExists() {
        CartModel cart = new CartModel(1L, 1L, 5);
        cart.setId(1L);

        when(repository.findById(1L)).thenReturn(Mono.just(item1));
        when(cartRepository.findByItemIdAndUserId(1L, 1L)).thenReturn(Mono.just(cart));

        itemService.getById(1L, 1)
                .as(StepVerifier::create)
                .assertNext(result -> {
                    assertThat(result).isNotNull();
                    assertThat(result.id()).isEqualTo(1L);
                    assertThat(result.title()).isEqualTo("Apple");
                    assertThat(result.description()).isEqualTo("Fresh red apple");
                    assertThat(result.price()).isEqualTo(100);
                    assertThat(result.imgPath()).isEqualTo("/images/apple.jpg");
                    assertThat(result.count()).isEqualTo(5);
                })
                .verifyComplete();

        verify(repository).findById(1L);
    }

    @Test
    void getById_ShouldReturnItemWithZeroCount_WhenNotInCart() {
        when(repository.findById(1L)).thenReturn(Mono.just(item1));
        when(cartRepository.findByItemIdAndUserId(1L, 1L)).thenReturn(Mono.empty());

        itemService.getById(1L, 1)
                .as(StepVerifier::create)
                .assertNext(result -> {
                    assertThat(result).isNotNull();
                    assertThat(result.id()).isEqualTo(1L);
                    assertThat(result.count()).isEqualTo(0);
                })
                .verifyComplete();
    }

    @Test
    void getById_ShouldThrowNotFoundException_WhenItemDoesNotExist() {
        when(repository.findById(999L)).thenReturn(Mono.empty());

        itemService.getById(999L, 1)
                .as(StepVerifier::create)
                .expectError(ResponseStatusException.class)
                .verify();

        verify(repository).findById(999L);
    }

    @Test
    void getAll_ShouldReturnAllItems_WhenNoSearchAndDefaultSort() {
        when(repository.findAll()).thenReturn(Flux.just(item1, item2, item3));
        when(repository.count()).thenReturn(Mono.just(3L));
        when(cartRepository.findByItemIdAndUserId(any(), eq(1L))).thenReturn(Mono.empty());

        SearchRequest request = new SearchRequest(null, SortFieldEnum.NO, 1, 5);

        itemService.getAll(request)
                .as(StepVerifier::create)
                .assertNext(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.search()).isNull();
                    assertThat(response.sort()).isEqualTo(SortFieldEnum.NO);
                    assertThat(response.items()).hasSize(1);
                    assertThat(response.items().get(0)).hasSize(3);
                    assertThat(response.items().get(0).get(0).title()).isEqualTo("Apple");
                    assertThat(response.items().get(0).get(1).title()).isEqualTo("Banana");
                    assertThat(response.items().get(0).get(2).title()).isEqualTo("Cherry");

                    PagingDto paging = response.paging();
                    assertThat(paging.pageSize()).isEqualTo(5);
                    assertThat(paging.pageNumber()).isEqualTo(1);
                    assertThat(paging.hasPrevious()).isFalse();
                    assertThat(paging.hasNext()).isFalse();
                })
                .verifyComplete();
    }

    @Test
    void getAll_ShouldReturnFilteredItems_WhenSearchProvided() {
        when(repository.search("ap")).thenReturn(Flux.just(item1, item2));
        when(repository.countByKeyword("ap")).thenReturn(Mono.just(2L));
        when(cartRepository.findByItemIdAndUserId(any(), eq(1L))).thenReturn(Mono.empty());

        SearchRequest request = new SearchRequest("ap", SortFieldEnum.NO, 1, 5);

        itemService.getAll(request)
                .as(StepVerifier::create)
                .assertNext(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.search()).isEqualTo("ap");
                    assertThat(response.items()).hasSize(1);
                    assertThat(response.items().get(0).stream().filter(x -> x.id() != -1)).hasSize(2);
                    assertThat(response.items().get(0).get(0).title()).isEqualTo("Apple");
                    assertThat(response.items().get(0).get(1).title()).isEqualTo("Banana");
                })
                .verifyComplete();
    }

    @Test
    void getAll_ShouldPaginateCorrectly() {
        when(repository.findAll()).thenReturn(Flux.just(item1, item2, item3));
        when(repository.count()).thenReturn(Mono.just(3L));
        when(cartRepository.findByItemIdAndUserId(any(), eq(1L))).thenReturn(Mono.empty());

        SearchRequest request = new SearchRequest(null, SortFieldEnum.NO, 2, 2);

        itemService.getAll(request)
                .as(StepVerifier::create)
                .assertNext(response -> {
                    PagingDto paging = response.paging();
                    assertThat(paging.pageNumber()).isEqualTo(2);
                    assertThat(paging.pageSize()).isEqualTo(2);
                })
                .verifyComplete();
    }

    @Test
    void getAll_ShouldSortByAlphabeticalOrder() {
        when(repository.findAll()).thenReturn(Flux.just(item1, item2, item3));
        when(repository.count()).thenReturn(Mono.just(3L));
        when(cartRepository.findByItemIdAndUserId(any(), eq(1L))).thenReturn(Mono.empty());

        SearchRequest request = new SearchRequest(null, SortFieldEnum.ALPHA, 1, 10);

        itemService.getAll(request)
                .as(StepVerifier::create)
                .assertNext(response -> {
                    assertThat(response.sort()).isEqualTo(SortFieldEnum.ALPHA);
                    assertThat(response.items().get(0).get(0).title()).isEqualTo("Apple");
                    assertThat(response.items().get(0).get(1).title()).isEqualTo("Banana");
                    assertThat(response.items().get(0).get(2).title()).isEqualTo("Cherry");
                })
                .verifyComplete();
    }

    @Test
    void getAll_ShouldSortByPrice() {
        when(repository.findAll()).thenReturn(Flux.just(item2, item1, item3));
        when(repository.count()).thenReturn(Mono.just(3L));
        when(cartRepository.findByItemIdAndUserId(any(), eq(1L))).thenReturn(Mono.empty());

        SearchRequest request = new SearchRequest(null, SortFieldEnum.PRICE, 1, 10);

        itemService.getAll(request)
                .as(StepVerifier::create)
                .assertNext(response -> {
                    assertThat(response.sort()).isEqualTo(SortFieldEnum.PRICE);
                    assertThat(response.items().get(0).get(0).title()).isEqualTo("Banana");
                    assertThat(response.items().get(0).get(1).title()).isEqualTo("Apple");
                    assertThat(response.items().get(0).get(2).title()).isEqualTo("Cherry");
                })
                .verifyComplete();
    }

    @Test
    void getAll_ShouldHaveHasNextAndHasPreviousFlags() {
        when(repository.findAll()).thenReturn(Flux.just(item1, item2, item3));
        when(repository.count()).thenReturn(Mono.just(5L));
        when(cartRepository.findByItemIdAndUserId(any(), eq(1L))).thenReturn(Mono.empty());

        SearchRequest request = new SearchRequest(null, SortFieldEnum.NO, 2, 2);

        itemService.getAll(request)
                .as(StepVerifier::create)
                .assertNext(response -> {
                    assertThat(response.paging().hasPrevious()).isTrue();
                    assertThat(response.paging().hasNext()).isTrue();
                })
                .verifyComplete();
    }

    @Test
    void getAll_ShouldFillChunksWithEmptyItems() {
        when(repository.findAll()).thenReturn(Flux.just(item1, item2));
        when(repository.count()).thenReturn(Mono.just(2L));
        when(cartRepository.findByItemIdAndUserId(any(), eq(1L))).thenReturn(Mono.empty());

        SearchRequest request = new SearchRequest(null, SortFieldEnum.NO, 1, 5);

        itemService.getAll(request)
                .as(StepVerifier::create)
                .assertNext(response -> {
                    assertThat(response.items().get(0)).hasSize(3);
                    assertThat(response.items().get(0).get(0).id()).isEqualTo(1L);
                    assertThat(response.items().get(0).get(1).id()).isEqualTo(2L);
                    assertThat(response.items().get(0).get(2).id()).isEqualTo(-1);
                    assertThat(response.items().get(0).get(2).title()).isEmpty();
                })
                .verifyComplete();
    }
}
