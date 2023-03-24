package com.salatin;

import java.util.ArrayDeque;
import java.util.Deque;

public class TwapCalculator {
    private final long windowSizeMs;
    private final Deque<Double> askPrices;
    private final Deque<Double> bidPrices;
    private final Deque<Long> timeStamps;

    public TwapCalculator(long windowSizeMs) {
        this.windowSizeMs = windowSizeMs;
        this.askPrices = new ArrayDeque<>();
        this.bidPrices = new ArrayDeque<>();
        this.timeStamps = new ArrayDeque<>();
    }

    public Twap calculate(long startProcessingTime, double bid, double ask) {
        collectPrices(ask, bid);

        boolean timeWindowStarted =
            (System.currentTimeMillis() - windowSizeMs) > startProcessingTime;

        if (timeWindowStarted) {
            clearOutOfWindowData();

            return createTickTwap();
        }

        return new Twap(0, 0);
    }

    private void collectPrices(double bid, double ask) {
        timeStamps.addFirst(System.currentTimeMillis());
        bidPrices.addFirst(bid);
        askPrices.addFirst(ask);
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

    private Twap createTickTwap() {
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

        return new Twap(askTwap, bidTwap);
    }
}
