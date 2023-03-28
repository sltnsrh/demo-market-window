package com.salatin.util;

import com.salatin.model.Tick;
import java.math.BigDecimal;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LineParser {
    private static final short TICKER_INDEX = 0;
    private static final short BID_INDEX = 1;
    private static final short BID_VOLUME_INDEX = 2;
    private static final short ASK_INDEX = 3;
    private static final short ASK_VOLUME_INDEX = 4;
    private static final String SPLIT_REGEX = ",";
    private static final Logger logger = Logger.getLogger(LineParser.class.getName());

    public static Tick getTickData(String line) {
        String[] fields = line.split(SPLIT_REGEX);

        String ticker;
        BigDecimal bid;
        int bidVolume;
        BigDecimal ask;
        int askVolume;

        try {
            ticker = fields[TICKER_INDEX];
            bid = new BigDecimal(fields[BID_INDEX]);
            bidVolume = Integer.parseInt(fields[BID_VOLUME_INDEX]);
            ask = new BigDecimal(fields[ASK_INDEX]);
            askVolume = Integer.parseInt(fields[ASK_VOLUME_INDEX]);
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            logger.log(Level.WARNING, () -> "Incorrect input data: " + line);

            return null;
        }

        if (ask.compareTo(bid) <= 0) {
            logger.log(Level.WARNING, () -> "Bad input data, ask can't be less or equal bid.: " + line);

            return null;
        }

        if (
            bid.multiply(ask).compareTo(BigDecimal.ZERO) <= 0
                || bidVolume * askVolume <= 0
        ) {
            logger.log(Level.WARNING, () -> "Prices or volumes can't be less or equal zero: " + line);

            return null;
        }

        if (ticker.isBlank()) {
            logger.log(Level.WARNING, () -> "Ticker data is empty: " + line);

            return null;
        }

        return new Tick(ticker, bid, bidVolume, ask, askVolume);
    }
}
