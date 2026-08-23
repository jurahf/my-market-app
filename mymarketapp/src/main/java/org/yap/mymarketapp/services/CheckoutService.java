package org.yap.mymarketapp.services;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.yap.mymarketapp.model.OrderItem;
import org.yap.mymarketapp.model.OrderModel;
import org.yap.mymarketapp.openapi.ApiClient;
import org.yap.mymarketapp.openapi.api.BalanceApi;
import org.yap.mymarketapp.openapi.model.BalanceDecRequest;
import org.yap.mymarketapp.repositories.CartRepository;
import org.yap.mymarketapp.repositories.ItemRepository;
import org.yap.mymarketapp.repositories.OrderItemRepository;
import org.yap.mymarketapp.repositories.OrderRepository;
import org.yap.mymarketapp.security.CurrentUserService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class CheckoutService {

    private final OrderRepository orderRepository;

    private final OrderItemRepository orderItemRepository;

    private final CartRepository cartRepository;

    private final ItemRepository itemRepository;

    private final BalanceApi balanceApi;

    private final CurrentUserService currentUser;

    public CheckoutService(OrderRepository orderRepository, OrderItemRepository orderItemRepository,
                           CartRepository cartRepository, ItemRepository itemRepository, BalanceApi balanceApi,
                           CurrentUserService currentUser) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.cartRepository = cartRepository;
        this.itemRepository = itemRepository;
        this.balanceApi = balanceApi;
        this.currentUser = currentUser;
    }

    public Mono<Long> createOrder() {
        return currentUser.getCurrentUserId()
                .flatMap(userId -> cartRepository.findAllByUserId(userId).collectList()
                        .filter(list -> !list.isEmpty())
                        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST)))
                        .flatMap(cartList -> {

                            OrderModel newOrder = new OrderModel();
                            newOrder.setUserId(userId);
                            newOrder.setTotalSum(0);

                            List<Mono<OrderItem>> itemMonos = new ArrayList<>();
                            List<Mono<Long>> priceMonos = new ArrayList<>();

                            for (var cart : cartList) {
                                itemMonos.add(Mono.just(new OrderItem(0L, cart.getItemId(), cart.getCount())));
                                priceMonos.add(itemRepository.findById(cart.getItemId())
                                        .map(item -> cart.getCount() * item.getPrice()));
                            }

                            return Mono.zip(priceMonos, prices ->
                                    Arrays.stream(prices)
                                            .mapToLong(p -> (Long) p)
                                            .sum()
                            ).flatMap(totalSum -> {
                                newOrder.setTotalSum(totalSum);
                                return balanceApi.decBalance(new BalanceDecRequest().decValue(totalSum))
                                        .filter(balance -> balance >= 0)
                                        .onErrorResume(e -> Mono.empty())
                                        .then(orderRepository.save(newOrder));
                            }).flatMap(savedOrder ->
                                    Flux.fromIterable(cartList)
                                            .flatMap(cart -> orderItemRepository.save(
                                                    new OrderItem(savedOrder.getId(), cart.getItemId(), cart.getCount())))
                                            .collectList()
                                            .then(cartRepository.deleteAllByUserId(userId))
                                            .thenReturn(savedOrder.getId())
                            );
                        })
                );
    }
}
