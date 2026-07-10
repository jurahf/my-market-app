package org.yap.mymarketapp.services;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.yap.mymarketapp.dtos.CartActionEnum;
import org.yap.mymarketapp.dtos.CartResponse;
import org.yap.mymarketapp.dtos.ItemDto;
import org.yap.mymarketapp.model.CartModel;
import org.yap.mymarketapp.model.ItemModel;
import org.yap.mymarketapp.repositories.CartRepository;
import org.yap.mymarketapp.repositories.ItemRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CartService {

    private final CartRepository repository;

    private final ItemRepository itemRepository;

    public CartService(CartRepository repository, ItemRepository itemRepository) {
        this.repository = repository;
        this.itemRepository = itemRepository;
    }

    @Transactional
    public void toCart(long itemId, CartActionEnum action) {
        ItemModel item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        Optional<CartModel> cartOpt = repository.findByItemId(itemId);

        if (cartOpt.isEmpty()) {
            if (action == CartActionEnum.PLUS) {
                var cart = new CartModel(item, 1);
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
                if (cart.getItem() != null)
                    cart.getItem().setCart(null);

                repository.deleteById(cart.getId());
            }
            else
                repository.save(cart);
        }
    }

    @Transactional(readOnly = true)
    public CartResponse getItemsInCart() {
        List<CartModel> cartList = repository.findAll();

        List<ItemDto> itemDtos = new ArrayList<ItemDto>();
        long totalSum = 0;

        for (var cart : cartList) {
            itemDtos.add(new ItemDto(
                cart.getItem().getId(),
                cart.getItem().getTitle(),
                cart.getItem().getDescription(),
                cart.getItem().getImgPath(),
                cart.getItem().getPrice(),
                cart.getCount()
            ));

            totalSum += cart.getCount() * cart.getItem().getPrice();
        }

        return new CartResponse(itemDtos, totalSum);
    }

}
