package org.yap.mymarketapp.model;

import jakarta.persistence.*;

/// Товары в корзине. Корзина у нас только одна, поэтому делаем просто 1 к 1
@Entity
@Table(name = "cart")
public class CartModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "item_id", nullable = false)
    private long itemId;

    @Column(name = "count", nullable = false)
    private int count;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "item_id", referencedColumnName = "id", insertable = false, updatable = false)
    private ItemModel Item;

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

    public ItemModel getItem() {
        return Item;
    }

    public void setItem(ItemModel item) {
        Item = item;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
