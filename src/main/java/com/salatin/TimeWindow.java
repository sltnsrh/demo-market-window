//package com.salatin;
//
//import java.util.Collections;
//import java.util.TreeMap;
//
//public class TimeWindow {
//    private final long size;
//    private final TreeMap<Double, TickTwap> bidBook;
//    private final TreeMap<Double, TickTwap> askBook;
//
//    public TimeWindow(long size) {
//        this.size = size;
//        this.bidBook = new TreeMap<>(Collections.reverseOrder());
//        this.askBook = new TreeMap<>();
//    }
//
//    public void addBidTick(TickTwap tickTwap) {
//        long minPossibleTime = System.currentTimeMillis() - size;
//
//        while (!bidBook.isEmpty() && bidBook.firstEntry().getValue().timestamp() < minPossibleTime) {
//            bidBook.remove(bidBook.firstKey());
//        }
//
//        bidBook.put(tickTwap.price(), tickTwap);
//    }
//
//    public void addAskTick(TickTwap tickTwap) {
//        long minPossibleTime = System.currentTimeMillis() - size;
//
//        while (!askBook.isEmpty() && askBook.firstEntry().getValue().timestamp() < minPossibleTime) {
//            askBook.remove(askBook.firstKey());
//        }
//
//        askBook.put(tickTwap.price(), tickTwap);
//    }
//
//    public long getSize() {
//        return size;
//    }
//
//    public TreeMap<Double, TickTwap> getBidBook() {
//        return bidBook;
//    }
//
//    public TreeMap<Double, TickTwap> getAskBook() {
//        return askBook;
//    }
//}
