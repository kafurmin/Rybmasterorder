package com.example.kif.rybmasterorder;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Kif on 12.12.2016.
 */

public class Order implements Parcelable {

    private int OrderId;
    private String Name;
    private String Mobile;
    private int Price;
    private Boolean Done;
    private Boolean Type;//true if spinnig, false if reels
    private String Date;
    private String MasterName;

    private String Detail;

    public void setOrderId(int orderId) {
        OrderId = orderId;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setMobile(String mobile) {
        Mobile = mobile;
    }

    public void setPrice(int price) {
        Price = price;
    }

    public void setDone(Boolean done) {
        Done = done;
    }

    public void setType(Boolean type) {
        Type = type;
    }

    public void setDate(String date) {
        Date = date;
    }

    public void setMasterName(String masterName) {
        MasterName = masterName;
    }

    public void setDetail(String detail) {
        Detail = detail;
    }

    public Order() {
    }

    public int getOrderId() {
        return OrderId;
    }

    public String getName() {
        return Name;
    }

    public String getMobile() {
        return Mobile;
    }

    public int getPrice() {
        return Price;
    }

    public Boolean getDone() {
        return Done;
    }

    public Boolean getType() {
        return Type;
    }

    public String getDate() {
        return Date;
    }

    public String getMasterName() {
        return MasterName;
    }

    public String getDetail() {
        return Detail;
    }

    public Order(int orderId, String name, String mobile, int price, Boolean done, Boolean type, String date, String masterName, String detail) {
        OrderId = orderId;
        Name = name;
        Mobile = mobile;
        Price = price;
        Done = done;
        Type = type;
        Date = date;
        MasterName = masterName;
        Detail = detail;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.OrderId);
        dest.writeString(this.Name);
        dest.writeString(this.Mobile);
        dest.writeInt(this.Price);
        dest.writeValue(this.Done);
        dest.writeValue(this.Type);
        dest.writeString(this.Date);
        dest.writeString(this.MasterName);
        dest.writeString(this.Detail);
    }

    protected Order(Parcel in) {
        this.OrderId = in.readInt();
        this.Name = in.readString();
        this.Mobile = in.readString();
        this.Price = in.readInt();
        this.Done = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.Type = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.Date = in.readString();
        this.MasterName = in.readString();
        this.Detail = in.readString();
    }

    public static final Creator<Order> CREATOR = new Creator<Order>() {
        @Override
        public Order createFromParcel(Parcel source) {
            return new Order(source);
        }

        @Override
        public Order[] newArray(int size) {
            return new Order[size];
        }
    };
}

