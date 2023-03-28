package com.salatin.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class MetricsCalculatorTest {

    @Test
    void bidAskImbalanceWithValidBidAskVolumesExpectBidAskImbalanceIsCorrect() {
        int bidVolume = 150;
        int askVolume = 50;
        double expected = 0.75;

        double actual = MetricsCalculator.bidAskImbalance(bidVolume, askVolume);

        assertEquals(expected, actual);
    }

    @Test
    void spreadWithValidPricesExpectCorrectSpread() {
        BigDecimal bid = BigDecimal.valueOf(0.010);
        BigDecimal ask = BigDecimal.valueOf(0.020);
        int expected = 10;

        int actual = MetricsCalculator.spread(bid, ask, "BRENT");

        assertEquals(expected, actual);
    }
}
