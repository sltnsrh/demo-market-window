package com.salatin.util;

public final class MetricsCalculator {

    private static final double TICK_SIZE = 0.001;

    private MetricsCalculator() {}

    public static double bidAskImbalance(int bidVolume, int askVolume) {
        return (double) bidVolume / (bidVolume + askVolume);
    }

    public static int spread(double bid, double ask) {
        return (int) Math.round((ask - bid) / TICK_SIZE);
    }
}
