package org.yap.mymarketapp.dtos;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ItemDto {

    private long id;

    private String title;

    private String description;

    private String imgPath;

    private long price;

    private int count;

    @JsonCreator
    public ItemDto(@JsonProperty("id") long id,
                   @JsonProperty("title") String title,
                   @JsonProperty("description") String description,
                   @JsonProperty("imgPath") String imgPath,
                   @JsonProperty("price") long price,
                   @JsonProperty("count") int count) {
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
