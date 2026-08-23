package org.yap.mymarketapp.services;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.yap.mymarketapp.dtos.ItemDto;
import org.yap.mymarketapp.dtos.OrderDto;
import org.yap.mymarketapp.model.OrderModel;
import org.yap.mymarketapp.repositories.OrderItemRepository;
import org.yap.mymarketapp.repositories.OrderRepository;
import org.yap.mymarketapp.repositories.ItemRepository;
import org.yap.mymarketapp.security.CurrentUserService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    private final OrderItemRepository orderItemRepository;

    private final ItemRepository itemRepository;

    private final CurrentUserService currentUser;

    public OrderService(OrderRepository orderRepository, OrderItemRepository orderItemRepository,
                        ItemRepository itemRepository, CurrentUserService currentUser) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.itemRepository = itemRepository;
        this.currentUser = currentUser;
    }

    public Flux<OrderDto> getAll() {
        return currentUser.getCurrentUserId()
                .flatMapMany(orderRepository::findAllByUserId)
                .flatMap(this::convertToDto);
    }

    public Mono<OrderDto> getById(long id) {
        return currentUser.getCurrentUserId()
                .flatMap(userId -> orderRepository.findByIdAndUserId(id, userId))
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .flatMap(this::convertToDto);
    }

    private Mono<OrderDto> convertToDto(OrderModel order) {
        return orderItemRepository.findByOrderId(order.getId())
                .flatMap(orderItem -> itemRepository.findById(orderItem.getItemId())
                        .map(item -> new ItemDto(
                                item.getId(),
                                item.getTitle(),
                                item.getDescription(),
                                item.getImgPath(),
                                item.getPrice(),
                                orderItem.getCount()
                        ))
                )
                .collectList()
                .map(items -> new OrderDto(order.getId(), items, order.getTotalSum()));
    }

}
