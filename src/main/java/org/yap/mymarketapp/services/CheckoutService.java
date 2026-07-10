package org.yap.mymarketapp.services;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.yap.mymarketapp.model.OrderItem;
import org.yap.mymarketapp.model.OrderModel;
import org.yap.mymarketapp.repositories.CartRepository;
import org.yap.mymarketapp.repositories.OrderRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class CheckoutService {

    private final OrderRepository orderRepository;

    private final CartRepository cartRepository;


    public CheckoutService(OrderRepository orderRepository, CartRepository cartRepository) {
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
    }

    /// Новый заказ - берем все, что было в корзине, и переносим в заказ. Корзину очищаем
    @Transactional
    public long createOrder() {
        var itemsList = cartRepository.findAll();
        if (itemsList.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

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

        var saved = orderRepository.save(newOrder);
        cartRepository.deleteAll();

        return saved.getId();
    }


}
