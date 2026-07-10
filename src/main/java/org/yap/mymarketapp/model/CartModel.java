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

    @Column(name = "count", nullable = false)
    private int count;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "item_id", referencedColumnName = "id")
    private ItemModel item;

    public CartModel() {

    }

    public CartModel(ItemModel item, int count) {
        this.item = item;
        this.count = count;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ItemModel getItem() {
        return item;
    }

    public void setItem(ItemModel item) {
        this.item = item;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
