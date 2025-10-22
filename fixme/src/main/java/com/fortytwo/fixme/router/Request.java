package com.fortytwo.fixme.router;

import com.fortytwo.fixme.router.OrderType.OrderType;

public class Request {
    private long senderId;
    private long targetId;
    private String symbol;
    private OrderType orderType;
    private int quantity;
    private int price;
    private boolean isValid;

    public void setIdValid() {
        this.isValid = true;
    }

    public boolean getIsValid() {
        return this.isValid;
    }

    public long getSenderId() {
        return senderId;
    }

    public void setSenderId(long senderId) {
        this.senderId = senderId;
    }

    public long getTargetId() {
        return targetId;
    }

    public void setTargetId(long targetId) {
        this.targetId = targetId;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public OrderType getOrderType() {
        return orderType;
    }

    public void setOrderType(OrderType orderType) {
        this.orderType = orderType;
    }

    public Request(long senderId, long targetId, String symbol, int price, int quantity) {
        this.senderId = senderId;
        this.targetId = targetId;
        this.symbol = symbol;
        this.price = price;
        this.quantity = quantity;
        this.orderType = OrderType.BUY;
    }
}
