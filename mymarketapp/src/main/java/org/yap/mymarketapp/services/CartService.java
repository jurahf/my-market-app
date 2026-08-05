package org.yap.mymarketapp.services;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.yap.mymarketapp.dtos.CartActionEnum;
import org.yap.mymarketapp.dtos.CartResponse;
import org.yap.mymarketapp.dtos.ItemDto;
import org.yap.mymarketapp.model.CartModel;
import org.yap.mymarketapp.openapi.ApiClient;
import org.yap.mymarketapp.openapi.api.BalanceApi;
import org.yap.mymarketapp.repositories.CartRepository;
import org.yap.mymarketapp.repositories.ItemRepository;
import reactor.core.publisher.Mono;

@Service
public class CartService {

    private final CartRepository repository;

    private final ItemRepository itemRepository;

    private final ApiClient apiClient;

    public CartService(CartRepository repository, ItemRepository itemRepository, ApiClient apiClient) {
        this.repository = repository;
        this.itemRepository = itemRepository;
        this.apiClient = apiClient;
    }

    public Mono<Void> toCart(long itemId, CartActionEnum action) {
        return itemRepository.findById(itemId)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .flatMap(item -> repository.findByItemId(itemId)
                        .flatMap(cart -> {
                            if (action == CartActionEnum.PLUS) {
                                cart.setCount(cart.getCount() + 1);
                            } else if (action == CartActionEnum.MINUS) {
                                cart.setCount(cart.getCount() - 1);
                            } else {
                                cart.setCount(0);
                            }

                            if (cart.getCount() <= 0) {
                                return repository.deleteById(cart.getId()).then();
                            }
                            return repository.save(cart);
                        })
                        .switchIfEmpty(Mono.defer(() -> {
                            if (action == CartActionEnum.PLUS) {
                                return repository.save(new CartModel(itemId, 1)).then();
                            }
                            return Mono.empty();
                        }))
                )
                .then();
    }

    public Mono<CartResponse> getItemsInCart() {
        return repository.findAll()
                .flatMap(cart -> itemRepository.findById(cart.getItemId())
                        .map(item -> new ItemDto(
                                item.getId(),
                                item.getTitle(),
                                item.getDescription(),
                                item.getImgPath(),
                                item.getPrice(),
                                cart.getCount()
                        ))
                )
                .collectList()
                .map(items -> {

                    long total = items.stream().mapToLong(i -> i.price() * i.count()).sum();

                    var api = new BalanceApi(apiClient);
                    var balance = api.getBalance();

                    return new CartResponse(items, total, balance);
                });
    }

}
