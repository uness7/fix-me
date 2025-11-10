package com.fortytwo.fixme.broker;

import com.fortytwo.fixme.common.Client;
import com.fortytwo.fixme.common.Utils;

import java.io.IOException;

public class Broker extends Client {

    public Broker(String name) {
        super(name);
    }

    public static void main(String[] args) {
        Broker br = new Broker("BRK1");
        try {
            br.activate(Utils.BROKER_PORT);
        } catch (IOException e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }
}
