package com.salatin;

import com.salatin.model.Tick;
import com.salatin.model.Twap;
import com.salatin.util.MetricsCalculator;

public class TickProcessor {
    private final TwapWindow twapWindow;

    public TickProcessor(TwapWindow twapWindow) {
        this.twapWindow = twapWindow;
    }

    public void process(Tick tick) {

        double bid = tick.bid();
        int bidVolume = tick.bidVolume();
        double ask = tick.ask();
        int askVolume = tick.askVolume();
        String ticker = tick.ticker();

        int spread = MetricsCalculator.spread(bid, ask);
        double imbalance = MetricsCalculator.bidAskImbalance(bidVolume, askVolume);
        Twap twap = twapWindow.calculateCurrentTick(bid, ask);

        System.out.printf(
            "%s: Bid: %.3f\t%d\t\tAsk: %.3f\t%d\t\tSpread: %d\t\tImbalance: %.2f\t\tTWAP: Bid: %.3f\tAsk: %.3f%n",
            ticker, bid, bidVolume, ask, askVolume,
            spread,
            imbalance,
            twap.bid(), twap.ask()
        );
    }
}
