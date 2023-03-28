package com.salatin;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.salatin.model.Tick;
import com.salatin.model.Twap;
import com.salatin.util.MetricsCalculator;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

class TickProcessorTest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final TwapWindow twapWindow = Mockito.mock(TwapWindow.class);

    @BeforeEach
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    public void restoreStreams() {
        System.setOut(System.out);
    }

    @Test
    void processAndOutWithValidTickDataExpectedValidSystemOutput() {
        var bid = BigDecimal.valueOf(70.110);
        var ask = BigDecimal.valueOf(70.115);
        var bidVolume = 100;
        var askVolume = 300;

        Tick tick = new Tick("BRENT", bid, bidVolume, ask, askVolume);
        TickProcessor tickProcessor = new TickProcessor(twapWindow);

        try (MockedStatic<MetricsCalculator> mc = Mockito.mockStatic(MetricsCalculator.class)) {
            mc.when(() -> MetricsCalculator.spread(bid, ask)).thenReturn(5);
            mc.when(() -> MetricsCalculator.bidAskImbalance(bidVolume, askVolume))
                    .thenReturn(0.25);

            Mockito.when(twapWindow.calculateCurrentTick(bid, ask))
                .thenReturn(new Twap(BigDecimal.ZERO, BigDecimal.ZERO));

            tickProcessor.processAndOut(tick);

            String expected = "BRENT: Bid: 70.110\t100\t\tAsk: 70.115\t300\t\t"
                + "Spread: 5\t\tImbalance: 0.25\t\tTWAP: Bid: 0.000\tAsk: 0.000";

            assertEquals(expected, outContent.toString().trim());
        }
    }
}
