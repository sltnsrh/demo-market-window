package com.salatin.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class LineParserTest {

    @Test
    void getTickDataWithValidDataExpectedNotNull() {
        String input = "USD/EUR,1.0523,2000,1.0527,600";

        assertNotNull(LineParser.getTickData(input));
    }

    @Test
    void getTickDataWithoutTickerInfoExpectedNull() {
        String input = "1.0523,2000,1.0527,600";

        assertNull(LineParser.getTickData(input));
    }

    @Test
    void getTickDataWithEmptyTickerInfoExpectedNull() {
        String input = "  ,1.0523,2000,1.0527,600";

        assertNull(LineParser.getTickData(input));
    }

    @Test
    void getTickDataWithEmptyLineExpectedNull() {
        String input = "";

        assertNull(LineParser.getTickData(input));
    }

    @Test
    void getTickDataWithBadFormatDataExpectedNull() {
        String input = "100,bad,data,not,numeric";

        assertNull(LineParser.getTickData(input));
    }

    @Test
    void getTickDataWithZeroBidExpectedNull() {
        String input = "USD/EUR,0.0000,2000,1.0527,600";

        assertNull(LineParser.getTickData(input));
    }

    @Test
    void getTickDataWithNegativeAskExpectedNull() {
        String input = "USD/EUR,1.0523,2000,-1.0527,600";

        assertNull(LineParser.getTickData(input));
    }

    @Test
    void getTickDataWithNegativeAskVolumeExpectedNull() {
        String input = "USD/EUR,1.0523,2000,1.0527,-600";

        assertNull(LineParser.getTickData(input));
    }

    @Test
    void getTickDataWithZeroBidVolumeExpectedNull() {
        String input = "USD/EUR,1.0523,0,1.0527,600";

        assertNull(LineParser.getTickData(input));
    }

    @Test
    void getTickDataWithEmptyBidExpectedNull() {
        String input = "USD/EUR,,2000,1.0527,600";

        assertNull(LineParser.getTickData(input));
    }
}
