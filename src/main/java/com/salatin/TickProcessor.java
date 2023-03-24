package com.salatin;

import com.salatin.util.MetricsCalculator;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class TickProcessor {
    private final TwapCalculator twapCalculator;

    private long startProcessingTime;

    public TickProcessor(TwapCalculator twapCalculator) {
        this.twapCalculator = twapCalculator;
        this.startProcessingTime = 0;
    }

    public void process(Tick tick) {
        if (startProcessingTime == 0) {
            startProcessingTime = System.currentTimeMillis();
        }

        double bid = tick.bid();
        int bidVolume = tick.bidVolume();
        double ask = tick.ask();
        int askVolume = tick.askVolume();
        String ticker = tick.ticker();

        Callable<Integer> spreadCallable = () -> MetricsCalculator.spread(bid, ask);
        Callable<Double> imbalanceCallable = () -> MetricsCalculator.bidAskImbalance(bidVolume, askVolume);
        Callable<Twap> twapCallable = () -> twapCalculator.calculate(startProcessingTime, bid, ask);

        ExecutorService executor = Executors.newFixedThreadPool(3);

        Future<Integer> spreadFuture = executor.submit(spreadCallable);
        Future<Double> imbalanceFuture = executor.submit(imbalanceCallable);
        Future<Twap> twapFuture = executor.submit(twapCallable);

        executor.shutdown();

        int spread;
        double imbalance;
        Twap twap;

        try {
            spread = spreadFuture.get();
            imbalance = imbalanceFuture.get();
            twap = twapFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        System.out.printf(
            "%s: Bid: %.3f\t%d\t\tAsk: %.3f\t%d\t\tSpread: %d\t\tImbalance: %.2f\t\tTWAP: Bid: %.3f\tAsk: %.3f%n",
            ticker, bid, bidVolume, ask, askVolume,
            spread,
            imbalance,
            twap.bid(), twap.ask()
        );
    }
}
