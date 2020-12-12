package com.fireextinguisher.model;

public class ServiceModel {

    private int id;
    private String serviceName;
    private boolean isSelected;

    public ServiceModel(int id, String serviceName, boolean isSelected) {
        this.id = id;
        this.serviceName = serviceName;
        this.isSelected = isSelected;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
