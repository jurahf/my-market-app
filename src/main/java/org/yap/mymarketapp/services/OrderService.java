package org.yap.mymarketapp.services;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.yap.mymarketapp.dtos.ItemDto;
import org.yap.mymarketapp.dtos.OrderDto;
import org.yap.mymarketapp.model.OrderModel;
import org.yap.mymarketapp.repositories.OrderRepository;

import java.util.List;

@Service
public class OrderService {

    private final OrderRepository repository;

    public OrderService(OrderRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<OrderDto> getAll() {
        List<OrderModel> modelList = repository.findAll();

        return modelList.stream()
                .map(x -> convertToDto(x))
                .toList();
    }

    @Transactional(readOnly = true)
    public OrderDto getById(long id) {
        OrderModel order = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return convertToDto(order);
    }


    private OrderDto convertToDto(OrderModel x) {
        return new OrderDto(
                x.getId(),
                x.getOrderItems().stream()
                        .map(y -> y.getItem())
                        .map(y -> new ItemDto(
                                y.getId(),
                                y.getTitle(),
                                y.getDescription(),
                                y.getImgPath(),
                                y.getPrice(),
                                repository.getItemCountInOrder(y.getId(), x.getId()).orElse(0)
                        ))
                        .toList(),
                x.getTotalSum()
                );
    }

}
