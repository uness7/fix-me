package com.fortytwo.fixme.router;

public abstract class Handler {
    private Handler next;

    public static Handler add(Handler first, Handler... chain) {
        Handler head = first;

        for (Handler handler : chain) {
            head.next = handler;
            head = handler;
        }

        return first;
    }

    // each handler in the chain will have the same input data
    public abstract boolean check();

    protected boolean hasNext() {
        return next.check();
    }
}
