package com.salatin;

import static org.junit.jupiter.api.Assertions.*;

import com.salatin.model.Twap;
import com.salatin.processing.TwapWindow;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class TwapWindowTest {

    @Test
    void calculateCurrentTickWithNotStartedTimeWindowExpectedAskAndBidTwapZero() {
        TwapWindow twapWindow = new TwapWindow(System.currentTimeMillis(), 60000);

        Twap actual = twapWindow.calculateCurrentTick(BigDecimal.TEN, BigDecimal.TEN);

        assertEquals(BigDecimal.ZERO, actual.ask());
        assertEquals(BigDecimal.ZERO, actual.bid());
    }

    @Test
    void calculateCurrentTickWithTwoTicksInsideTimeWindowExpectedTwapAskIsHigherBid() {
        TwapWindow twapWindow = new TwapWindow(
            0,
            500
        );

        var bidFirst = BigDecimal.valueOf(100.1110);
        var bidSecond = BigDecimal.valueOf(100.1160);
        var askFirst = BigDecimal.valueOf(100.1230);
        var askSecond = BigDecimal.valueOf(100.1280);

        twapWindow.calculateCurrentTick(bidFirst, askFirst);

        try {
            Thread.sleep(250);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        Twap actual = twapWindow.calculateCurrentTick(bidSecond, askSecond);

        assertTrue(actual.ask().compareTo(actual.bid()) > 0);
    }

}
