package com.fireextinguisher.model;

import java.io.Serializable;

public class FirePumpModel implements Serializable {

    private int id;
    private String name;
    private String imageURL;
    private boolean isSelected;

    public FirePumpModel(int id, String name, String imageURL, boolean isSelected) {
        this.id = id;
        this.name = name;
        this.imageURL = imageURL;
        this.isSelected = isSelected;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
