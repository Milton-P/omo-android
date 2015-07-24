package com.nightwind.meal.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by nightwind on 15/4/13.
 */
public class Order /*implements Parcelable*/ {
    private int id;
    private String username;
    private String address;
    private String tel;
    private int dishId;
    private int dishCount;
    //当时dish的价钱
    private double dishCost;
    private double cost;
    private String status;
//    private Date time;
//    private Date confirmTime;
//    private Date sendTime;
    private String time;
    private String confirmTime;
    private String sendTime;
    //此时的dish
    private Dish dish;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public int getDishId() {
        return dishId;
    }

    public void setDishId(int dishId) {
        this.dishId = dishId;
    }

    public int getDishCount() {
        return dishCount;
    }

    public void setDishCount(int dishCount) {
        this.dishCount = dishCount;
    }

    public double getDishCost() {
        return dishCost;
    }

    public void setDishCost(double dishCost) {
        this.dishCost = dishCost;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

//    public Date getTime() {
//        return time;
//    }
//
//    public void setTime(Date time) {
//        this.time = time;
//    }
//
//    public Date getConfirmTime() {
//        return confirmTime;
//    }
//
//    public void setConfirmTime(Date confirmTime) {
//        this.confirmTime = confirmTime;
//    }
//
//    public Date getSendTime() {
//        return sendTime;
//    }
//
//    public void setSendTime(Date sendTime) {
//        this.sendTime = sendTime;
//    }


    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getConfirmTime() {
        return confirmTime;
    }

    public void setConfirmTime(String confirmTime) {
        this.confirmTime = confirmTime;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    public Dish getDish() {
        return dish;
    }

    public void setDish(Dish dish) {
        this.dish = dish;
    }

    public Order() {
    }

//    public Order(Parcel source) {
//        id = source.readInt();
//        username = source.readString();
//        address = source.readString();
//        tel = source.readString();
//        dishId = source.readInt();
//        dishCount = source.readInt();
//        dishCost = source.readDouble();
//        cost = source.readDouble();
//        status = source.readString();
//        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
//        String strTime = source.readString();
//        try {
//            time = df.parse(strTime);
//            confirmTime = df.parse(source.readString());
//            sendTime = df.parse(source.readString());
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        dish = source.read
//    }
//
//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    @Override
//    public void writeToParcel(Parcel dest, int flags) {
//        dest.writeInt(id);
//        dest.writeInt(username);
//        dest.writeInt(address);
//        dest.writeInt(tel);
//        dest.writeInt(dishId);
//        dest.writeInt(dishCount);
//        dest.writeInt(id);
//    }

//    public static final Creator<Order> CREATOR = new Creator<Order>() {
//        @Override
//        public Order createFromParcel(Parcel source) {
//            return new Order(source);
//        }
//
//        @Override
//        public Order[] newArray(int size) {
//            return new Order[size];
//        }
//    };
}
