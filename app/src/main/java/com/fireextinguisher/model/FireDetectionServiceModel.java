package com.fireextinguisher.model;

public class FireDetectionServiceModel {

    private int id;
    private String serviceName;
    private boolean isSelected;
    private String location;
    private String qty;
    private String remark;

    public FireDetectionServiceModel(int id, String serviceName, boolean isSelected, String location, String qty, String remark) {
        this.id = id;
        this.serviceName = serviceName;
        this.isSelected = isSelected;
        this.location = location;
        this.qty = qty;
        this.remark = remark;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}