package com.fortytwo.fixme;


import com.fortytwo.fixme.broker.Broker;
import com.fortytwo.fixme.common.Instrument;
import com.fortytwo.fixme.common.Utils;
import com.fortytwo.fixme.market.Market;
import com.fortytwo.fixme.router.Router;

import java.io.IOException;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class App {
    private volatile static LinkedList<Instrument> instruments = new LinkedList<>();

    public static void init() {
        String path = "/home/waizi/Desktop/42PostCommonCore/fix-me/fixme/instruments.conf";
        instruments = Utils.getInstruments(path);
    }

    public static void launchSimulator() {
        Router router = Router.getInstance();
        try {
            router.activate();
        } catch (IOException e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    public static void main( String[] args ) throws IOException, InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(3);
        new Thread(App::launchSimulator).start();
        Thread.sleep(500);
        Broker br = new Broker("br");
        br.activate();
    }
}
