package com.example.orderfood.Model;

import java.util.List;

public class Request {
    private String Phone, Name, Address, Total, status, comment, paymentState;
    private List<Order> Foods;

    public Request() {
    }

    public Request(String phone, String name, String address, String total, String status, String comment, String paymentState, List<Order> foods) {
        Phone = phone;
        Name = name;
        Address = address;
        Total = total;
        this.status = status;
        this.comment = comment;
        this.paymentState = paymentState;
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
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getPaymentState() {
        return paymentState;
    }

    public void setPaymentState(String paymentState) {
        this.paymentState = paymentState;
    }

    public List<Order> getFoods() {
        return Foods;
    }

    public void setFoods(List<Order> foods) {
        Foods = foods;
    }
}
