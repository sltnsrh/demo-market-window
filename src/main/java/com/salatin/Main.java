package com.salatin;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Random;

public class Main {
    static final long TIME_WINDOW_SIZE = 10;

    static Deque<Double> askPrices = new ArrayDeque<>();
    static Deque<Double> bidPrices = new ArrayDeque<>();
    static Deque<Long> timeStamps = new ArrayDeque<>();

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
            askPrices.removeLast();
            bidPrices.removeLast();

            oldestTimeStamp = timeStamps.getLast();
        }
    }
}
