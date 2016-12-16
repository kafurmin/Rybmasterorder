package com.example.kif.rybmasterorder;

/**
 * Created by Kif on 12.12.2016.
 */

public class Orders {

    private Boolean Done;
    private int Mobile;
    private int OrderId;

    public Orders() {
    }

    public Orders(Boolean done, int mobile, int orderId) {
        OrderId = orderId;
        Mobile = mobile;
        Done = done;
    }

    public int getOrderId() {
        return OrderId;
    }

    public void setOrderId(int orderId) {
        OrderId = orderId;
    }

    public int getMobile() {
        return Mobile;
    }

    public void setMobile(int mobile) {
        Mobile = mobile;
    }

    public Boolean getDone() {
        return Done;
    }

    public void setDone(Boolean done) {
        Done = done;
    }
}
