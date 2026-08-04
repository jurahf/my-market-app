package org.yap.mymarketapp.services;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.yap.mymarketapp.model.OrderItem;
import org.yap.mymarketapp.model.OrderModel;
import org.yap.mymarketapp.repositories.CartRepository;
import org.yap.mymarketapp.repositories.ItemRepository;
import org.yap.mymarketapp.repositories.OrderItemRepository;
import org.yap.mymarketapp.repositories.OrderRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Service
public class CheckoutService {

    private final OrderRepository orderRepository;

    private final OrderItemRepository orderItemRepository;

    private final CartRepository cartRepository;

    private final ItemRepository itemRepository;

    public CheckoutService(OrderRepository orderRepository, OrderItemRepository orderItemRepository,
                           CartRepository cartRepository, ItemRepository itemRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.cartRepository = cartRepository;
        this.itemRepository = itemRepository;
    }

    public Mono<Long> createOrder() {
        return cartRepository.findAll().collectList()
                .filter(list -> !list.isEmpty())
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST)))
                .flatMap(cartList -> {
                    OrderModel newOrder = new OrderModel();
                    newOrder.setTotalSum(0);

                    List<Mono<OrderItem>> itemMonos = new ArrayList<>();
                    List<Mono<Long>> priceMonos = new ArrayList<>();

                    for (var cart : cartList) {
                        itemMonos.add(Mono.just(new OrderItem(0L, cart.getItemId(), cart.getCount())));
                        priceMonos.add(itemRepository.findById(cart.getItemId())
                                .map(item -> cart.getCount() * item.getPrice()));
                    }

                    return Mono.zip(priceMonos, prices -> {
                        long total = 0;
                        for (Object p : prices) {
                            total += (Long) p;
                        }
                        return total;
                    }).flatMap(totalSum -> {
                        newOrder.setTotalSum(totalSum);
                        return orderRepository.save(newOrder);
                    }).flatMap(savedOrder -> {
                        List<Mono<OrderItem>> saveMonos = new ArrayList<>();
                        for (var cart : cartList) {
                            saveMonos.add(orderItemRepository.save(
                                    new OrderItem(savedOrder.getId(), cart.getItemId(), cart.getCount())));
                        }
                        return Flux.merge(saveMonos).collectList()
                                .then(cartRepository.deleteAll())
                                .thenReturn(savedOrder.getId());
                    });
                });
    }

}
