package org.yap.mymarketapp.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("cart")
public class CartModel {

    @Id
    private long id;

    @Column("count")
    private int count;

    @Column("item_id")
    private long itemId;

    public CartModel() {
    }

    public CartModel(long itemId, int count) {
        this.itemId = itemId;
        this.count = count;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getItemId() {
        return itemId;
    }

    public void setItemId(long itemId) {
        this.itemId = itemId;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
