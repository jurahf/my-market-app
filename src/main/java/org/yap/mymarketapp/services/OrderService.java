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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    private final OrderItemRepository orderItemRepository;

    private final ItemRepository itemRepository;

    public OrderService(OrderRepository orderRepository, OrderItemRepository orderItemRepository,
                        ItemRepository itemRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.itemRepository = itemRepository;
    }

    public Flux<OrderDto> getAll() {
        return orderRepository.findAll()
                .flatMap(this::convertToDto);
    }

    public Mono<OrderDto> getById(long id) {
        return orderRepository.findById(id)
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
