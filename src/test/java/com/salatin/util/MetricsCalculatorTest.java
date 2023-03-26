package com.salatin.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
        double bid = 0.001;
        double ask = 0.010;
        int expected = 9;

        int actual = MetricsCalculator.spread(bid, ask);

        assertEquals(expected, actual);
    }
}