package com.fortytwo.fixme.common;

public class Instrument {
    private final String name;
    private final String company;
    private final String sector;
    private final int price; // in USD
    private final int quantity; // shares
    private final boolean isTraded;

    public Instrument(String name,
                      String company,
                      String sector,
                      int price,
                      int quantity,
                      boolean isTraded) {
        this.name = name;
        this.company = company;
        this.sector = sector;
        this.price = price;
        this.quantity = quantity;
        this.isTraded = isTraded;
    }

    public String toString() {
        return name + " " +  company + " " + sector + " " + price +
                " " + quantity + " " + isTraded;
    }
}