package com.nightwind.meal.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by nightwind on 15/4/12.
 */
public class Dish implements Parcelable {

    private int id;
    private String name;
    private String info;
    private double cost;
    private String picUrl;
    private String status;


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

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Dish() {
    }

    public Dish(Parcel source) {
        id = source.readInt();
        name = source.readString();
        info = source.readString();
        cost = source.readDouble();
        picUrl = source.readString();
        status = source.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(info);
        dest.writeDouble(cost);
        dest.writeString(picUrl);
        dest.writeString(status);
    }

    public static final Creator<Dish> CREATOR = new Creator<Dish>() {
        @Override
        public Dish createFromParcel(Parcel source) {
            return new Dish(source);
        }

        @Override
        public Dish[] newArray(int size) {
            return new Dish[size];
        }
    };



}
