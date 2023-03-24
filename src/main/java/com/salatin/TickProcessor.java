package com.salatin;

public class TickProcessor {
    private final Twap twap;

    private long startProcessingTime;

    public TickProcessor(Twap twap) {
        this.twap = twap;
        this.startProcessingTime = 0;
    }

    public void process(Tick tick) {
        if (startProcessingTime == 0) {
            startProcessingTime = System.currentTimeMillis();
        }

        twap.collectPrices(tick.ask(), tick.bid());
        TickTwap tickTwap = twap.output(startProcessingTime);

        outProcessedData(tick, tickTwap);
    }

    private void outProcessedData(Tick tick, TickTwap twap) {
        double bid = tick.bid();
        int bidVolume = tick.bidVolume();
        double ask = tick.ask();
        int askVolume = tick.askVolume();
        String ticker = tick.ticker();

        System.out.printf(
            "%s: Bid: %.3f\t%d\t\tAsk: %.3f\t%d\t\tSpread: %d\t\tImbalance: %.2f\t\tTWAP: Bid: %.3f\tAsk: %.3f%n",
            ticker, bid, bidVolume, ask, askVolume, spread(bid, ask), bidAskImbalance(bidVolume, askVolume),
            twap.bid(), twap.ask()
        );
    }

    private double bidAskImbalance(int bidVolume, int askVolume) {
        return (double) bidVolume / (bidVolume + askVolume);
    }

    private int spread(double bid, double ask) {
        return (int) ((ask - bid) / 0.001);
    }
}
