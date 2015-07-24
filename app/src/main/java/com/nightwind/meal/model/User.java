package com.nightwind.meal.model;

/**
 * Created by nightwind on 15/4/11.
 */
public class User {

    private String username;

//    private String identity;

    private Double balance;

    private String name;

    private String tel;

    private String address;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

//    public String getIdentity() {
//        return identity;
//    }
//
//    public void setIdentity(String identity) {
//        this.identity = identity;
//    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public User() {
    }

    public User(String username) {
        this.username = username;
    }
}
