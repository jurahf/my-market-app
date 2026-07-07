package org.yap.mymarketapp.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yap.mymarketapp.dtos.CartActionEnum;
import org.yap.mymarketapp.model.CartModel;
import org.yap.mymarketapp.model.ItemModel;
import org.yap.mymarketapp.repositories.CartRepository;

import java.util.List;
import java.util.Optional;

@Service
public class CartService {

    @Autowired
    public CartRepository repository;

    public void toCart(long itemId, CartActionEnum action) {
        // TODO: не проверяется существование товара с таким id, только лежит ли он в корзине

        Optional<CartModel> cartOpt = repository.findByItemId(itemId).stream().findFirst();

        if (cartOpt.isEmpty()) {
            if (action == CartActionEnum.PLUS) {
                var cart = new CartModel(itemId, 1);
                repository.save(cart);
            }
        }
        else {
            CartModel cart = cartOpt.get();
            if (action == CartActionEnum.PLUS) {
                cart.setCount(cart.getCount() + 1);
            }
            else {
                cart.setCount(cart.getCount() - 1);
            }

            if (cart.getCount() <= 0)
                repository.delete(cart);
            else
                repository.save(cart);
        }
    }

}
