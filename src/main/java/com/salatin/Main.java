package com.salatin;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.Random;
import java.util.TreeMap;

public class Main {
    static final long TIME_WINDOW_SIZE = 1000;

    static Deque<Double> askPrices = new ArrayDeque<>();
    static Deque<Double> bidPrices = new ArrayDeque<>();
    static Deque<Long> timeStamps = new ArrayDeque<>();

    static TreeMap<Double, Integer> askBook = new TreeMap<>();
    static TreeMap<Double, Integer> bidBook = new TreeMap<>(Collections.reverseOrder());

    public static void main(String[] args) {
        String inputFile = "/Users/ssalatin/Downloads/BRENT-prepared.csv";

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
            Random random = new Random();
            String line;

            long startProcessingTime = System.currentTimeMillis();

            while ((line = reader.readLine()) != null) {
                //Sleeping just for a testing, because all 57382 lines(1 day session ticks) processed in 1.5-2 seconds
                Thread.sleep(random.nextInt(10));

                String[] fields = line.split(",");

                double bid = Double.parseDouble(fields[1]);
                int bidVolume = Integer.parseInt(fields[2]);
                double ask = Double.parseDouble(fields[3]);
                int askVolume = Integer.parseInt(fields[4]);

                long startTimeWindow = System.currentTimeMillis() - TIME_WINDOW_SIZE;

                long currentTimeStamp = System.currentTimeMillis();
                timeStamps.addFirst(currentTimeStamp);
                askPrices.addFirst(ask);
                bidPrices.addFirst(bid);

                //top of book filling
                if (askBook.containsKey(ask)) {
                    int existingVolume = askBook.get(ask);
                    askBook.put(ask, existingVolume + askVolume);

                    askBook.headMap(ask).clear();
                } else {
                    askBook.put(ask, askVolume);

                    askBook.headMap(ask).clear();
                }

                if (bidBook.containsKey(bid)) {
                    int existingVolume = bidBook.get(bid);
                    bidBook.put(bid, existingVolume + bidVolume);

                    bidBook.headMap(bid).clear();
                } else {
                    bidBook.put(bid, bidVolume);

                    bidBook.headMap(bid).clear();
                }

                if (startTimeWindow > startProcessingTime) {
                    clearOldData(startTimeWindow);

                    TickTwap twap = createTickTwap();

                    System.out.printf("Ask: %.3f Bid: %.3f%n", twap.ask(), twap.bid());
                }

            }

            System.out.println("Processing time: " + (System.currentTimeMillis() - startProcessingTime));

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static TickTwap createTickTwap() {
        var timeStampsIterator = timeStamps.iterator();
        var askPricesIterator = askPrices.iterator();
        var bidPricesIterator = bidPrices.iterator();

        long prevTimeStamp = 0;
        double sumAsks = 0;
        double sumBids = 0;

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

            double askDeltaMultiply = askPrice * deltaTime;
            double bidDeltaMultiply = bidPrice * deltaTime;

            sumAsks += askDeltaMultiply;
            sumBids += bidDeltaMultiply;
        }

        double askTwap = sumAsks / TIME_WINDOW_SIZE;
        double bidTwap = sumBids / TIME_WINDOW_SIZE;

        return new TickTwap(askTwap, bidTwap);
    }

    public static void clearOldData(long startOfTimeWindow) {
        long oldestTimeStamp = timeStamps.getLast();
        while (oldestTimeStamp < startOfTimeWindow) {
            timeStamps.removeLast();
            var removedAsk = askPrices.removeLast();
            askBook.remove(removedAsk);
            var removedBid = bidPrices.removeLast();
            bidBook.remove(removedBid);

            oldestTimeStamp = timeStamps.getLast();
        }
    }
}
