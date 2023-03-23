package com.salatin;

import java.util.Collections;
import java.util.TreeMap;

public class MarketBook {
    private final TreeMap<Double, Integer> askBook = new TreeMap<>();
    private final TreeMap<Double, Integer> bidBook = new TreeMap<>(Collections.reverseOrder());

    public void update(Tick tick) {
        askBook.put(tick.ask(), tick.askVolume());
        askBook.headMap(tick.ask()).clear();

        bidBook.put(tick.bid(), tick.bidVolume());
        bidBook.headMap(tick.bid()).clear();
    }
}
