package org.yap.mymarketapp.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yap.mymarketapp.dtos.ItemDto;
import org.yap.mymarketapp.dtos.OrderDto;
import org.yap.mymarketapp.model.OrderModel;
import org.yap.mymarketapp.repositories.OrderRepository;

import java.util.List;

@Service
public class OrderService {

    @Autowired
    public OrderRepository repository;


    public List<OrderDto> getAll() {
        List<OrderModel> modelList = repository.findAll();

        return modelList.stream()
                .map(x -> convertToDto(x))
                .toList();
    }


    private OrderDto convertToDto(OrderModel x) {
        return new OrderDto(
                x.getId(),
                x.getItems().stream()
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
