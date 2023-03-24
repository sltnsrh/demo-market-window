package com.salatin;

import java.util.ArrayDeque;
import java.util.Deque;

public class Twap {
    private final long windowSizeMs;
    private final Deque<Double> askPrices;
    private final Deque<Double> bidPrices;
    private final Deque<Long> timeStamps;

    public Twap(long windowSizeMs) {
        this.windowSizeMs = windowSizeMs;
        this.askPrices = new ArrayDeque<>();
        this.bidPrices = new ArrayDeque<>();
        this.timeStamps = new ArrayDeque<>();
    }

    public void collectPrices(double ask, double bid) {
        timeStamps.addFirst(System.currentTimeMillis());
        askPrices.addFirst(ask);
        bidPrices.addFirst(bid);
    }

    public TickTwap output(long startProcessingTime) {
        boolean timeWindowStarted =
            (System.currentTimeMillis() - windowSizeMs) > startProcessingTime;

        if (timeWindowStarted) {
            clearOutOfWindowData();

            return createTickTwap();
        }

        return new TickTwap(0, 0);
    }

    private void clearOutOfWindowData() {
        if (timeStamps.isEmpty()) {
            return;
        }

        long oldestTimeStamp = timeStamps.getLast();
        long windowStartTime = System.currentTimeMillis() - windowSizeMs;

        while (oldestTimeStamp < windowStartTime) {
            timeStamps.removeLast();
            askPrices.removeLast();
            bidPrices.removeLast();

            if (!timeStamps.isEmpty()) {
                oldestTimeStamp = timeStamps.getLast();
            } else {
                return;
            }
        }
    }

    private TickTwap createTickTwap() {
        var timeStampsIterator = timeStamps.iterator();
        var askPricesIterator = askPrices.iterator();
        var bidPricesIterator = bidPrices.iterator();

        long prevTimeStamp = 0;
        double sumAsksDeltas = 0;
        double sumBidsDeltas = 0;

        while (
            timeStampsIterator.hasNext()
                && askPricesIterator.hasNext()
                && bidPricesIterator.hasNext()
        ) {
            if (prevTimeStamp == 0) {
                prevTimeStamp = timeStampsIterator.next();
                askPricesIterator.next();
                bidPricesIterator.next();
                continue;
            }

            long currentTime = timeStampsIterator.next();
            long deltaTime = Math.abs(currentTime - prevTimeStamp);
            prevTimeStamp = currentTime;

            double askPrice = askPricesIterator.next();
            double bidPrice = bidPricesIterator.next();

            sumAsksDeltas += askPrice * deltaTime;
            sumBidsDeltas += bidPrice * deltaTime;
        }

        double askTwap = sumAsksDeltas / windowSizeMs;
        double bidTwap = sumBidsDeltas / windowSizeMs;

        return new TickTwap(askTwap, bidTwap);
    }
}
