package com.example.serverside.Model;

import java.util.List;

public class Request {
    private String Phone, Name, Address, Total, Status, Comment;
    private Double Latitude,Longitude;
    private List<Order> Foods;

    public Request() {
    }

    public Request(String phone, String name, String address, String total, String status, String comment, Double latitude, Double longitude, List<Order> foods) {
        Phone = phone;
        Name = name;
        Address = address;
        Total = total;
        Status = status;
        Comment = comment;
        Latitude = latitude;
        Longitude = longitude;
        Foods = foods;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getTotal() {
        return Total;
    }

    public void setTotal(String total) {
        Total = total;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getComment() {
        return Comment;
    }

    public void setComment(String comment) {
        Comment = comment;
    }

    public Double getLatitude() {
        return Latitude;
    }

    public void setLatitude(Double latitude) {
        Latitude = latitude;
    }

    public Double getLongitude() {
        return Longitude;
    }

    public void setLongitude(Double longitude) {
        Longitude = longitude;
    }

    public List<Order> getFoods() {
        return Foods;
    }

    public void setFoods(List<Order> foods) {
        Foods = foods;
    }
}