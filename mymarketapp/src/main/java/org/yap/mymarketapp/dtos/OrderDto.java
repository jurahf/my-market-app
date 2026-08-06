package org.yap.mymarketapp.dtos;

import java.util.List;

public class OrderDto {

    private long id;

    private List<ItemDto> items;

    private long totalSum;

    public OrderDto(long id, List<ItemDto> items, long totalSum) {
        this.id = id;
        this.items = items;
        this.totalSum = totalSum;
    }

    public long id() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<ItemDto> items() {
        return items;
    }

    public void setItems(List<ItemDto> items) {
        this.items = items;
    }

    public long totalSum() {
        return totalSum;
    }

    public void setTotalSum(long totalSum) {
        this.totalSum = totalSum;
    }
}
