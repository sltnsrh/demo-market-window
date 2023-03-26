package com.salatin.util;

import com.salatin.model.Tick;
import java.math.BigDecimal;

public class LineParser {
    private static final short TICKER_INDEX = 0;
    private static final short BID_INDEX = 1;
    private static final short BID_VOLUME_INDEX = 2;
    private static final short ASK_INDEX = 3;
    private static final short ASK_VOLUME_INDEX = 4;
    private static final String SPLIT_REGEX = ",";

    public static Tick getTickData(String line) {
        String[] fields = line.split(SPLIT_REGEX);

        String ticker = fields[TICKER_INDEX];
        BigDecimal bid = new BigDecimal(fields[BID_INDEX]);
        int bidVolume = Integer.parseInt(fields[BID_VOLUME_INDEX]);
        BigDecimal ask = new BigDecimal(fields[ASK_INDEX]);
        int askVolume = Integer.parseInt(fields[ASK_VOLUME_INDEX]);

        return new Tick(ticker, bid, bidVolume, ask, askVolume);
    }
}
