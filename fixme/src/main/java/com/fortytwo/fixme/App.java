package com.fortytwo.fixme;


import com.fortytwo.fixme.common.Instrument;
import com.fortytwo.fixme.router.Router;

import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class App {
    private static final LinkedList<Instrument> instruments = new LinkedList<>();

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

    public static void main( String[] args ) {
        Router router = Router.getInstance();
        try (ExecutorService executor = Executors.newFixedThreadPool(2)) {
            Runnable task1 = () -> router.listen(router.BROKER_PORT);
            Runnable task2 = () -> router.listen(router.MARKET_PORT);
            executor.submit(task1);
            executor.submit(task2);
        }
    }
}
