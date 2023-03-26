package com.salatin.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class MetricsCalculator {

    private MetricsCalculator() {}

    public static double bidAskImbalance(int bidVolume, int askVolume) {
        return (double) bidVolume / (bidVolume + askVolume);
    }

    public static int spread(BigDecimal bid, BigDecimal ask) {
        int scale = Math.max(bid.scale(), ask.scale());

        BigDecimal tickSize = BigDecimal.valueOf(Math.pow(0.1, scale))
            .setScale(scale, RoundingMode.HALF_UP);

        return ask.subtract(bid)
            .divide(tickSize, RoundingMode.HALF_UP)
            .intValue();
    }
}
