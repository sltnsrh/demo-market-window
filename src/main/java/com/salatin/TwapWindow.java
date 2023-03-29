package com.salatin;

import com.salatin.model.Twap;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
        collectPrices(bid, ask);

        boolean timeWindowStarted =
            (System.currentTimeMillis() - windowSizeMs) > startProcessingTime;

        if (timeWindowStarted) {
            clearOutOfWindowData();

            if(timeStamps.size() > 1) {
                return createTickTwap();
            }
        }

        return new Twap(BigDecimal.ZERO, BigDecimal.ZERO);
    }

    private void collectPrices(BigDecimal bid, BigDecimal ask) {
        timeStamps.addFirst(System.currentTimeMillis());
        bidPrices.addFirst(bid);
        askPrices.addFirst(ask);
    }

    private void clearOutOfWindowData() {

        while ((timeStamps.getFirst() - timeStamps.getLast()) > windowSizeMs) {
            timeStamps.removeLast();
            askPrices.removeLast();
            bidPrices.removeLast();
        }
    }

    private Twap createTickTwap() {
        var timeStampsIterator = timeStamps.descendingIterator();
        var askPricesIterator = askPrices.descendingIterator();
        var bidPricesIterator = bidPrices.descendingIterator();

        long prevTimeStamp = 0;
        BigDecimal sumAsksDeltas = BigDecimal.valueOf(0);
        BigDecimal sumBidsDeltas = BigDecimal.valueOf(0);

        while (
            timeStampsIterator.hasNext()
                && askPricesIterator.hasNext()
                && bidPricesIterator.hasNext()
        ) {
            long deltaTime;
            long currentTime = timeStampsIterator.next();

            if (prevTimeStamp == 0) {
                deltaTime = (timeStamps.getFirst() - timeStamps.getLast()) / (timeStamps.size() - 1);
            } else {
                deltaTime = currentTime - prevTimeStamp;
            }

            prevTimeStamp = currentTime;

            BigDecimal askPrice = askPricesIterator.next();
            BigDecimal bidPrice = bidPricesIterator.next();

            sumAsksDeltas = sumAsksDeltas.add(askPrice.multiply(BigDecimal.valueOf(deltaTime)));
            sumBidsDeltas = sumBidsDeltas.add(bidPrice.multiply(BigDecimal.valueOf(deltaTime)));
        }

        BigDecimal askTwap = sumAsksDeltas
            .divide(BigDecimal.valueOf(windowSizeMs), RoundingMode.HALF_UP);
        BigDecimal bidTwap = sumBidsDeltas
            .divide(BigDecimal.valueOf(windowSizeMs), RoundingMode.HALF_UP);

        return new Twap(askTwap, bidTwap);
    }
}
