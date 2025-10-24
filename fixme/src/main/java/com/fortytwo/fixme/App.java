package com.fortytwo.fixme;

import com.fortytwo.fixme.common.Utils;
import com.fortytwo.fixme.market.Market;

import java.io.IOException;

class Main {
    public static void main(String[] args) {
        Market mr = new Market("Blue", Utils.getInstruments("~/notes.txt"));
        try {
            mr.activate();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
