package com.salatin.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class MetricsCalculator {

    private MetricsCalculator() {}

    public static double bidAskImbalance(int bidVolume, int askVolume) {
        return (double) bidVolume / (bidVolume + askVolume);
    }

    public static int spread(BigDecimal bid, BigDecimal ask, String ticker) {

        return ask.subtract(bid)
            .divide(SymbolInfo.TICK_MIN_SIZE.get(ticker), RoundingMode.HALF_UP)
            .intValue();
    }
}
