package org.yap.mymarketapp.dtos;

public class ItemDto {

    public long id;

    public String title;

    public String description;

    public String imgPath;

    public long price;

    public int count;

    public ItemDto(long id, String title, String description, String imgPath, long price, int count) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.imgPath = imgPath;
        this.price = price;
        this.count = count;
    }

    public long id() {
        return id;
    }

    public String title() {
        return title;
    }

    public String description() {
        return description;
    }

    public String imgPath() {
        return imgPath;
    }

    public long price() {
        return price;
    }

    public int count() {
        return count;
    }
}
