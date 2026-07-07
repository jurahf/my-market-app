package org.yap.mymarketapp.model;

import jakarta.persistence.*;

import java.util.Optional;

@Entity
@Table(name = "item")
public class ItemModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public long id;

    @Column(name = "title", nullable = false, length = 255)
    public String title;

    @Column(name = "description", columnDefinition = "TEXT")
    public String description;

    @Column(name = "img_path", length = 500)
    public String imgPath;

    @Column(name = "price", nullable = false)
    public long price;


    public Optional<CartModel> getCart() {
        return Optional.ofNullable(cart);
    }

    public void setCart(CartModel cart) {
        this.cart = cart;
    }

    @OneToOne(mappedBy = "Item", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private CartModel cart;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }
}
