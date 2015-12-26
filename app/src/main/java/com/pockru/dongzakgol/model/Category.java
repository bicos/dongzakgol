package com.pockru.dongzakgol.model;

/**
 * Created by rhpark on 15. 12. 26..
 */
public class Category {

    public String name;
    public String link;

    public Category(String name, String link) {
        this.name = name;
        this.link = link;
    }

    public Category() {
    }

    @Override
    public String toString() {
        return "Category{" +
                "name='" + name + '\'' +
                ", link='" + link + '\'' +
                '}';
    }
}
