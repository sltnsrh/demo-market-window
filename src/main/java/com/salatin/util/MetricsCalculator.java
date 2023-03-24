package com.salatin.util;

public final class MetricsCalculator {

    private MetricsCalculator() {}

    public static double bidAskImbalance(int bidVolume, int askVolume) {
        return (double) bidVolume / (bidVolume + askVolume);
    }

    public static int spread(double bid, double ask) {
        return (int) ((ask - bid) / 0.001);
    }
}
