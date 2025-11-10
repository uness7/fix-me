package com.fortytwo.fixme;

import com.fortytwo.fixme.common.Utils;
import com.fortytwo.fixme.market.Market;
import com.fortytwo.fixme.common.Instrument;
import java.util.ListIterator;

import java.io.IOException;
import java.util.LinkedList;

class Main {
    public static void main(String[] args) {
		LinkedList<Instrument> instruments = 
			Utils.getInstruments("config.txt");
		ListIterator<Instrument> it = instruments.listIterator();

		while (it.hasNext()) {
			Instrument ins = it.next();
			System.out.println(ins.toString());
		}

		/*
        try {
            mr.activate();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
		*/
    }
}
