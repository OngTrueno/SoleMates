package com.example.a15017470.solemates;

/**
 * Created by 15017470 on 10/7/2017.
 */

public class Blog {
    private String brand;
    private String model;
    private String image;

    public Blog(){

    }

    public Blog(String brand, String model, String image) {
        this.brand = brand;
        this.model = model;
        this.image = image;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
