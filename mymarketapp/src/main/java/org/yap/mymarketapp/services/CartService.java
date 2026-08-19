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
import org.yap.mymarketapp.security.CurrentUserService;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class CartService {

    private final CartRepository repository;

    private final ItemRepository itemRepository;

    private final BalanceApi balanceApi;

    private final CurrentUserService currentUser;

    public CartService(CartRepository repository, ItemRepository itemRepository, BalanceApi balanceApi,
                       CurrentUserService currentUser) {
        this.repository = repository;
        this.itemRepository = itemRepository;
        this.balanceApi = balanceApi;
        this.currentUser = currentUser;
    }

    public Mono<Void> toCart(long itemId, CartActionEnum action) {
        return currentUser.getCurrentUserId()
                .flatMap(userId -> itemRepository.findById(itemId)
                        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                        .flatMap(item -> repository.findByItemIdAndUserId(itemId, userId)
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
                                        return repository.save(new CartModel(userId, itemId, 1)).then();
                                    }
                                    return Mono.empty();
                                }))
                        )
                )
                .then();
    }

    public Mono<CartResponse> getItemsInCart() {
        return currentUser.getCurrentUserId()
                .flatMapMany(repository::findAllByUserId)
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
                .flatMap(items -> {
                    return balanceApi.getBalance()
                            .onErrorResume(x -> Mono.just(-1L))
                            .map(balance -> {
                                long total = items.stream().mapToLong(i -> i.price() * i.count()).sum();
                                return new CartResponse(items, total, balance);
                    });
                });
    }
}
