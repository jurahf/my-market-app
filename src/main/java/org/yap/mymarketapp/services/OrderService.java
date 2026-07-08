package org.yap.mymarketapp.services;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yap.mymarketapp.dtos.ItemDto;
import org.yap.mymarketapp.dtos.OrderDto;
import org.yap.mymarketapp.model.ItemModel;
import org.yap.mymarketapp.model.OrderItem;
import org.yap.mymarketapp.model.OrderModel;
import org.yap.mymarketapp.repositories.CartRepository;
import org.yap.mymarketapp.repositories.OrderRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    public OrderRepository repository;

    @Autowired
    public CartRepository cartRepository;


    public List<OrderDto> getAll() {
        List<OrderModel> modelList = repository.findAll();

        return modelList.stream()
                .map(x -> convertToDto(x))
                .toList();
    }

    public OrderDto getById(long id) {
        OrderModel order = repository.findById(id).orElseThrow();

        return convertToDto(order);
    }

    /// Новый заказ - берем все, что было в корзине, и переносим в заказ. Корзину очищаем
    @Transactional
    public long createOrder() {
        var itemsList = cartRepository.findAll();

        List<OrderItem> items = new ArrayList<>();
        OrderModel newOrder = new OrderModel();
        long totalSum = 0;

        for (var cart : itemsList) {
            items.add(new OrderItem(newOrder, cart.getItem(), cart.getCount()));

            cart.getItem().setCart(null);

            totalSum += cart.getCount() * cart.getItem().getPrice();
        }

        newOrder.setOrderItems(items);
        newOrder.setTotalSum(totalSum);

        var saved = repository.save(newOrder);
        cartRepository.deleteAll();

        return saved.getId();
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
