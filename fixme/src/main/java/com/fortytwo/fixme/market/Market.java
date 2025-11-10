package com.fortytwo.fixme.market;

import com.fortytwo.fixme.common.Client;
import com.fortytwo.fixme.common.Instrument;
import com.fortytwo.fixme.common.Utils;

import java.io.IOException;
import java.util.LinkedList;

public class Market extends Client {
    public LinkedList<Instrument> instrumentsList;

    public Market(String name, LinkedList<Instrument> instrumentsList) {
        super(name);
        this.instrumentsList = instrumentsList;
    }

    public static void main(String[] args) {
        Market market = new Market("mr", Utils.getInstruments("config.txt"));
        try {
            market.activate(Utils.MARKET_PORT);
        } catch (IOException e) {
            System.out.println("Exception " + e.getMessage());
        }
    }
}