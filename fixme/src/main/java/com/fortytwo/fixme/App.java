package com.fortytwo.fixme;

import com.fortytwo.fixme.broker.Broker;
import com.fortytwo.fixme.common.Utils;
import com.fortytwo.fixme.market.Market;

import java.io.IOException;

class Main {
    public static void main(String[] args) {
        Broker mr = new Broker("Brki");
        try {
            mr.activate(Utils.BROKER_PORT);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
