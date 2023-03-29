package com.salatin;

import com.salatin.input.CsvStream;
import com.salatin.input.DataStream;
import com.salatin.processing.TickProcessor;
import com.salatin.processing.TwapWindow;

public class Main {
    public static final String INPUT_FILE = "src/main/resources/BRENT-tick-data-input.csv";
    public static final long TIME_WINDOW_SIZE = 60000;

    public static void main(String[] args) {
        TwapWindow twapWindow = new TwapWindow(System.currentTimeMillis(), TIME_WINDOW_SIZE);
        TickProcessor tickProcessor = new TickProcessor(twapWindow);
        DataStream dataStream = new CsvStream(INPUT_FILE, tickProcessor);

        dataStream.start();
    }
}
