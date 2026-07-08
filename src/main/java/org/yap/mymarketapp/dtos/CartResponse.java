package org.yap.mymarketapp.dtos;

import java.util.List;

public class CartResponse {

    private List<ItemDto> items;

    private long total;

    public CartResponse(List<ItemDto> items, long total) {
        this.items = items;
        this.total = total;
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
}
