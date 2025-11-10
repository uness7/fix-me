package com.fortytwo.fixme;

import com.fortytwo.fixme.common.Utils;
import com.fortytwo.fixme.market.Market;
import com.fortytwo.fixme.common.Instrument;
import java.util.ListIterator;

import java.io.IOException;
import java.util.LinkedList;

class Main {
    public static void main(String[] args) {
        String CONF_FILE = "config.txt";

		LinkedList<Instrument> instruments = Utils.getInstruments(CONF_FILE);
        for (Instrument ins : instruments) {
            System.out.println(ins.toString());
        }
    }
}
