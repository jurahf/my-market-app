package org.yap.mymarketapp.services;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.yap.mymarketapp.config.RedisCacheConfig;
import org.yap.mymarketapp.dtos.*;
import org.yap.mymarketapp.model.ItemModel;
import org.yap.mymarketapp.repositories.CartRepository;
import org.yap.mymarketapp.repositories.ItemRepository;
import org.yap.mymarketapp.security.CurrentUserService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class ItemService {

    private final ItemRepository repository;

    private final CartRepository cartRepository;

    private final CurrentUserService currentUser;

    public ItemService(ItemRepository repository, CartRepository cartRepository, CurrentUserService currentUser) {
        this.repository = repository;
        this.cartRepository = cartRepository;
        this.currentUser = currentUser;
    }

    @Cacheable(cacheNames = RedisCacheConfig.ITEMS_CACHE, key = "#id")
    public Mono<ItemDto> getById(long id) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .flatMap(item -> currentUser.getCurrentUserId()
                        .flatMap(userId -> cartRepository.findByItemIdAndUserId(item.getId(), userId)
                                .map(cart -> convertFromDB(item, cart.getCount()))
                                .defaultIfEmpty(convertFromDB(item, 0)))
                        .switchIfEmpty(Mono.defer(() -> Mono.just(convertFromDB(item, 0))))
                );
    }

    @Cacheable(cacheNames = RedisCacheConfig.ITEMS_CACHE, key = "#request")
    public Mono<SearchResponse> getAll(SearchRequest request) {
        return currentUser.getCurrentUserId()
                .flatMap(userId -> buildSearchResponse(request, userId))
                .switchIfEmpty(Mono.defer(() -> buildSearchResponse(request, null)));
    }

    private Mono<SearchResponse> buildSearchResponse(SearchRequest request, Long userId) {
        Comparator<ItemModel> comparator = getComparator(request.sort());

        Mono<List<ItemModel>> itemsMono;
        Mono<Long> countMono;

        if (request.search() != null && !request.search().isEmpty()) {
            itemsMono = repository.search(request.search())
                    .sort(comparator)
                    .collectList();
            countMono = repository.countByKeyword(request.search());
        } else {
            itemsMono = repository.findAll()
                    .sort(comparator)
                    .collectList();
            countMono = repository.count();
        }

        return Mono.zip(itemsMono, countMono)
                .flatMap(tuple -> {
                    List<ItemModel> allItems = tuple.getT1();
                    long totalElements = tuple.getT2();

                    int pageNumber = request.pageNumber() < 1 ? 1 : request.pageNumber();
                    int pageSize = request.pageSize() <= 0 ? 5 : request.pageSize();

                    int start = (pageNumber - 1) * pageSize;
                    int end = Math.min(start + pageSize, allItems.size());

                    List<ItemModel> pageItems = start < allItems.size()
                            ? allItems.subList(start, end)
                            : List.of();

                    Flux<ItemDto> pageDtos = userId == null
                            ? Flux.fromIterable(pageItems).map(item -> convertFromDB(item, 0))
                            : Flux.fromIterable(pageItems)
                                    .flatMap(item -> cartRepository.findByItemIdAndUserId(item.getId(), userId)
                                            .map(cart -> convertFromDB(item, cart.getCount()))
                                            .defaultIfEmpty(convertFromDB(item, 0)));

                    return pageDtos.collectList()
                            .map(dtos -> {
                                var items = splitIntoChunks(dtos, 3);
                                boolean hasPrevious = pageNumber > 1;
                                boolean hasNext = end < totalElements;

                                return new SearchResponse(
                                        request.search(),
                                        request.sort(),
                                        new PagingDto(pageSize, pageNumber, hasPrevious, hasNext),
                                        items);
                            });
                });
    }

    private ItemDto convertFromDB(ItemModel x, int cartCount) {
        return new ItemDto(
                x.getId(),
                x.getTitle(),
                x.getDescription(),
                x.getImgPath(),
                x.getPrice(),
                cartCount
        );
    }

    private List<List<ItemDto>> splitIntoChunks(List<ItemDto> list, int chunkSize) {
        return IntStream.range(0, (list.size() + chunkSize - 1) / chunkSize)
                .mapToObj(i -> {
                    int start = i * chunkSize;
                    int end = Math.min(start + chunkSize, list.size());
                    List<ItemDto> chunk = new ArrayList<>(list.subList(start, end));

                    while (chunk.size() < chunkSize) {
                        chunk.add(new ItemDto(-1, "", "", "", 0, 0));
                    }

                    return chunk;
                })
                .collect(Collectors.toList());
    }

    private Comparator<ItemModel> getComparator(SortFieldEnum sort) {
        if (sort == null || sort == SortFieldEnum.NO) {
            return (a, b) -> 0;
        }

        return switch (sort) {
            case ALPHA -> Comparator.comparing(ItemModel::getTitle);
            case PRICE -> Comparator.comparingLong(ItemModel::getPrice);
            default -> (a, b) -> 0;
        };
    }

}
