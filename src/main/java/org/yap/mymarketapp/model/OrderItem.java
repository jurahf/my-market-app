package org.yap.mymarketapp.model;

import jakarta.persistence.*;

@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id", referencedColumnName = "id")
    private OrderModel order;

    @ManyToOne
    @JoinColumn(name = "item_id", referencedColumnName = "id")
    private ItemModel item;

    @Column(nullable = false)
    private int count;

    public OrderItem() {
    }

    public OrderItem(OrderModel order, ItemModel item, int count) {
        this.order = order;
        this.item = item;
        this.count = count;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OrderModel getOrder() {
        return order;
    }

    public void setOrder(OrderModel order) {
        this.order = order;
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
