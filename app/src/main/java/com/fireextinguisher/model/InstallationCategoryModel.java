package com.fireextinguisher.model;

import java.io.Serializable;

public class InstallationCategoryModel implements Serializable {

    private String id;
    private String name;
    private String imageURL;
    private boolean isSelected;

    public InstallationCategoryModel(String id, String name, String imageURL, boolean isSelected) {
        this.id = id;
        this.name = name;
        this.imageURL = imageURL;
        this.isSelected = isSelected;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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
