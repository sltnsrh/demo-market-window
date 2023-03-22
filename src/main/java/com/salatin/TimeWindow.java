package com.salatin;

import java.util.Collections;
import java.util.TreeMap;

public class TimeWindow {
    private final long size;
    private final TreeMap<Double, Tick> bidBook;
    private final TreeMap<Double, Tick> askBook;

    public TimeWindow(long size) {
        this.size = size;
        this.bidBook = new TreeMap<>(Collections.reverseOrder());
        this.askBook = new TreeMap<>();
    }

    public void addBidTick(Tick tick) {
        long minPossibleTime = System.currentTimeMillis() - size;

        while (!bidBook.isEmpty() && bidBook.firstEntry().getValue().timestamp() < minPossibleTime) {
            bidBook.remove(bidBook.firstKey());
        }

        bidBook.put(tick.price(), tick);
    }

    public void addAskTick(Tick tick) {
        long minPossibleTime = System.currentTimeMillis() - size;

        while (!askBook.isEmpty() && askBook.firstEntry().getValue().timestamp() < minPossibleTime) {
            askBook.remove(askBook.firstKey());
        }

        askBook.put(tick.price(), tick);
    }

    public long getSize() {
        return size;
    }

    public TreeMap<Double, Tick> getBidBook() {
        return bidBook;
    }

    public TreeMap<Double, Tick> getAskBook() {
        return askBook;
    }
}
