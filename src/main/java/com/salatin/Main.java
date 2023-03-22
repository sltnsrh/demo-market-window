package com.salatin;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

public class Main {
    static final long TIME_WINDOW_SIZE = 5000;

    public static void main(String[] args) {
        String inputFile = "/Users/ssalatin/Downloads/BRENT-prepared.csv";
        String outputFile = "/output.csv";

        TimeWindow timeWindow = new TimeWindow(TIME_WINDOW_SIZE);

        double askTwap = 0;
        double bidTwap = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");

                double bid = Double.parseDouble(fields[1]);
                int bidVolume = Integer.parseInt(fields[2]);
                double ask = Double.parseDouble(fields[3]);
                int askVolume = Integer.parseInt(fields[4]);

                timeWindow.addBidTick(new Tick(System.currentTimeMillis(), bid, bidVolume));
                timeWindow.addAskTick(new Tick(System.currentTimeMillis(), ask, askVolume));

                AtomicLong prevAskTimeStamp = new AtomicLong(timeWindow.getAskBook().firstEntry().getValue().timestamp());

                double sumAsksDeltas = timeWindow.getBidBook().entrySet().stream()
                    .mapToDouble(tick -> {
                        double multiply = tick.getValue().price() * (tick.getValue().timestamp() - prevAskTimeStamp.get());
                        prevAskTimeStamp.set(tick.getValue().timestamp());
                        return multiply;
                    })
                    .sum();

                askTwap = sumAsksDeltas / timeWindow.getSize();

                AtomicLong prevBidTimeStamp = new AtomicLong(timeWindow.getBidBook().firstEntry().getValue().timestamp());

                double sumBidsDeltas = timeWindow.getBidBook().entrySet().stream()
                    .mapToDouble(tick -> {
                        double multiply = tick.getValue().price() * (tick.getValue().timestamp() - prevBidTimeStamp.get());
                        prevBidTimeStamp.set(tick.getValue().timestamp());
                        return multiply;
                    })
                    .sum();

                bidTwap = sumBidsDeltas / timeWindow.getSize();

//                String outputLine = String.format("BRENT,%.3f,%d,%.3f,%d\n", bid, bidVolume, ask, askVolume);
                System.out.printf("Ask: %f3, Bid: %f3%n", askTwap, bidTwap);
            }

            System.out.println("Data processed successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}