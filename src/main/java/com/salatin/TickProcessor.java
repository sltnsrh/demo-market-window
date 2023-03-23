package com.salatin;

public class TickProcessor {
    private final MarketBook marketBook;
    private final Twap twap;

    private long startProcessingTime;

    public TickProcessor(MarketBook marketBook, Twap twap) {
        this.marketBook = marketBook;
        this.twap = twap;
        this.startProcessingTime = 0;
    }

    public void process(Tick tick) {
        if (startProcessingTime == 0) {
            startProcessingTime = System.currentTimeMillis();
        }

        twap.collectPrices(tick.ask(), tick.bid());
        marketBook.update(tick);

        System.out.println(twap.output(startProcessingTime));
    }
}
