package org.yap.mymarketapp.dtos;

import java.util.List;

public class CartResponse {

    private List<ItemDto> items;

    private long total;

    private long balance;

    public CartResponse(List<ItemDto> items, long total, long balance) {
        this.items = items;
        this.total = total;
        this.balance = balance;
    }

    public List<ItemDto> getItems() {
        return items;
    }

    public void setItems(List<ItemDto> items) {
        this.items = items;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getBalance() {
        return balance;
    }

    public void setBalance(long balance) {
        this.balance = balance;
    }
}
