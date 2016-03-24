package com.pockru.dongzakgol.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Ravy on 16. 3. 20..
 */
public class Category extends RealmObject {

    @PrimaryKey
    private Long id;
    private String key;
    private String name;
    private Long order;
    private Long favOrder;
    private boolean isFavorite = false;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getOrder() {
        return order;
    }

    public void setOrder(Long order) {
        this.order = order;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setIsFavorite(boolean isFavorite) {
        this.isFavorite = isFavorite;
    }

    public Long getFavOrder() {
        return favOrder;
    }

    public void setFavOrder(Long favOrder) {
        this.favOrder = favOrder;
    }

    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", key='" + key + '\'' +
                ", name='" + name + '\'' +
                ", order=" + order +
                ", favOrder=" + favOrder +
                ", isFavorite=" + isFavorite +
                '}';
    }
}
