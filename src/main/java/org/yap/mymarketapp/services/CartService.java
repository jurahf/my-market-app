package org.yap.mymarketapp.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yap.mymarketapp.dtos.CartActionEnum;
import org.yap.mymarketapp.dtos.CartResponse;
import org.yap.mymarketapp.dtos.ItemDto;
import org.yap.mymarketapp.model.CartModel;
import org.yap.mymarketapp.repositories.CartRepository;

import java.util.ArrayList;
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
            else if (action == CartActionEnum.MINUS) {
                cart.setCount(cart.getCount() - 1);
            }
            else { // DELETE
                cart.setCount(0);
            }

            if (cart.getCount() <= 0) {
                cart.getItem().setCart(null);
                repository.deleteById(cart.id);
            }
            else
                repository.save(cart);
        }
    }

    public CartResponse getItemsInCart() {
        List<CartModel> cartList = repository.findAll();

        List<ItemDto> itemDtos = new ArrayList<ItemDto>();
        long totalSum = 0;

        for (var cart : cartList) {
            itemDtos.add(new ItemDto(
                cart.itemId,
                cart.Item.title,
                cart.Item.description,
                cart.Item.imgPath,
                cart.Item.price,
                cart.count
            ));

            totalSum += cart.count * cart.Item.price;
        }

        return new CartResponse(itemDtos, totalSum);
    }

}
