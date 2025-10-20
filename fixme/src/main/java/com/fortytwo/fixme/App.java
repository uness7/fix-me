package com.fortytwo.fixme;


import com.fortytwo.fixme.router.Router;

import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class App {
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
