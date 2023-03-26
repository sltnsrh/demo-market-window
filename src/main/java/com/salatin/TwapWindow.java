package com.salatin;

import com.salatin.model.Twap;
import java.math.BigDecimal;
import java.util.ArrayDeque;
import java.util.Deque;

public class TwapWindow {
    private final long startProcessingTime;
    private final long windowSizeMs;
    private final Deque<BigDecimal> askPrices;
    private final Deque<BigDecimal> bidPrices;
    private final Deque<Long> timeStamps;

    public TwapWindow(long startProcessingTime, long windowSizeMs) {
        this.startProcessingTime = startProcessingTime;
        this.windowSizeMs = windowSizeMs;
        this.askPrices = new ArrayDeque<>();
        this.bidPrices = new ArrayDeque<>();
        this.timeStamps = new ArrayDeque<>();
    }

    public Twap calculateCurrentTick(BigDecimal bid, BigDecimal ask) {
        collectPrices(ask, bid);

        boolean timeWindowStarted =
            (System.currentTimeMillis() - windowSizeMs) > startProcessingTime;

        if (timeWindowStarted) {
            clearOutOfWindowData();

            return createTickTwap();
        }

        return new Twap(BigDecimal.ZERO, BigDecimal.ZERO);
    }

    private void collectPrices(BigDecimal bid, BigDecimal ask) {
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
        BigDecimal sumAsksDeltas = BigDecimal.valueOf(0);
        BigDecimal sumBidsDeltas = BigDecimal.valueOf(0);

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

            BigDecimal askPrice = askPricesIterator.next();
            BigDecimal bidPrice = bidPricesIterator.next();

            sumAsksDeltas.add(askPrice.multiply(BigDecimal.valueOf(deltaTime)));
            sumBidsDeltas.add(bidPrice.multiply(BigDecimal.valueOf(deltaTime)));
        }

        BigDecimal askTwap = sumAsksDeltas.divide(BigDecimal.valueOf(windowSizeMs));
        BigDecimal bidTwap = sumBidsDeltas.divide(BigDecimal.valueOf(windowSizeMs));

        return new Twap(askTwap, bidTwap);
    }
}
