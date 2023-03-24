package com.salatin;

import com.salatin.input.CsvStream;
import com.salatin.input.DataStream;

public class Main {
    public static final String INPUT_FILE = "src/main/resources/BRENT-tick-data-input.csv";
    public static final long TIME_WINDOW_SIZE = 10000;

    public static void main(String[] args) {
        Twap twap = new Twap(TIME_WINDOW_SIZE);
        TickProcessor tickProcessor = new TickProcessor(twap);
        DataStream dataStream = new CsvStream(INPUT_FILE, tickProcessor);

        dataStream.start();
    }
}
