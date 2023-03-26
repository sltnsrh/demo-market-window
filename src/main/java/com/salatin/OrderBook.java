package com.salatin;

import com.salatin.model.Tick;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class OrderBook {
    private final TreeMap<Double, Integer> askBook = new TreeMap<>();
    private final TreeMap<Double, Integer> bidBook = new TreeMap<>(Collections.reverseOrder());

    public void update(Tick tick) {
        askBook.put(tick.ask(), tick.askVolume());
        askBook.headMap(tick.ask()).clear();

        bidBook.put(tick.bid(), tick.bidVolume());
        bidBook.headMap(tick.bid()).clear();
    }

    /**
     * @param depthSize a depth of one side of the order book
     * @return a String formatted snippet of a current state of the order book
     */

    public String getMarketDepthSnapshot(short depthSize) {
        StringBuilder builder = new StringBuilder();
        builder.append("BID\t\tPRICE\t\tASK");
        builder.append(System.lineSeparator());

        List< Map.Entry<Double, Integer>> askEntries = new ArrayList<>(askBook.entrySet().stream()
            .limit(depthSize)
            .toList());

        Collections.reverse(askEntries);

        askEntries.forEach(ask -> {
                builder.append("   \t\t").append(ask.getKey()).append("\t\t").append(ask.getValue());
                builder.append(System.lineSeparator());
            });

        bidBook.entrySet().stream().limit(depthSize)
            .forEach(bid -> {
            builder.append(bid.getValue()).append("\t\t").append(bid.getKey());
            builder.append(System.lineSeparator());
        });

        return builder.toString();
    }
}
