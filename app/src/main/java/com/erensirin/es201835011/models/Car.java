package com.erensirin.es201835011.models;

public class Car {
    private String key;
    private String car;
    private String cardetail;
    private String price;
    private String startdate;
    private String enddate;
    private String photo;

    public Car() {
    }

    public Car(String key, String car, String cardetail, String price, String startdate, String enddate, String photo) {
        this.key = key;
        this.car = car;
        this.cardetail = cardetail;
        this.price = price;
        this.startdate = startdate;
        this.enddate = enddate;
        this.photo = photo;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getCar() {
        return car;
    }

    public void setCar(String car) {
        this.car = car;
    }

    public String getCardetail() {
        return cardetail;
    }

    public void setCardetail(String cardetail) {
        this.cardetail = cardetail;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getStartdate() {
        return startdate;
    }

    public void setStartdate(String startdate) {
        this.startdate = startdate;
    }

    public String getEnddate() {
        return enddate;
    }

    public void setEnddate(String enddate) {
        this.enddate = enddate;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}