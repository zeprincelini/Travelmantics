package com.lini.travelmantics;

import java.io.Serializable;

public class TravelDeal implements Serializable{
    private String Id;
    private String Title;
    private String Price;
    private String Description;
    private String ImageUrl;
    private String ImageName;

    public TravelDeal(){}
    public TravelDeal(String title, String price, String description, String imageUrl, String imageName) {
        Title = title;
        Price = price;
        Description = description;
        ImageUrl = imageUrl;
        ImageName = imageName;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getImageName() {
        return ImageName;
    }

    public void setImageName(String imageName) {
        ImageName = imageName;
    }
}
