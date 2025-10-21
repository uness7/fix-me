package com.fortytwo.fixme;


import com.fortytwo.fixme.broker.Broker;
import com.fortytwo.fixme.common.Instrument;
import com.fortytwo.fixme.router.Router;

import java.io.IOException;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class App {
    private volatile static LinkedList<Instrument> instruments = new LinkedList<>();

    public static void init() {
        // TODO create a function that constructs the list of instruments from a CSV file
        Instrument msft = new Instrument(
                "MSFT",
                "Microsoft Inc.",
                "Technology",
                90,
                25,
                true
        );
        Instrument aapl = new Instrument(
                "AAPL",
                "Apple Inc.",
                "Technology",
                100,
                20,
                true
        );
        instruments.add(msft);
    }

    public static void launchSimulator() {
        Router router = Router.getInstance();
        router.activate();
    }

    public static void main( String[] args ) throws IOException {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        Runnable t1 = App::launchSimulator;
        Runnable t2 = () -> {
            Broker br = new Broker("br");
            try {
                br.init();
                Thread.sleep(TimeUnit.SECONDS.toMillis(1));
                br.listenForAckMessage();

                if (br.getUniqueId() != -1) {
                    br.sendBuyRequest("Hello");
                }
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        };
        executor.submit(t1);
        executor.submit(t2);
    }
}
