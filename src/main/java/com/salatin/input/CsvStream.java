package com.salatin.input;

import com.salatin.model.Tick;
import com.salatin.TickProcessor;
import com.salatin.util.LineParser;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

public class CsvStream implements DataStream {
    private final String inputPath;
    private final TickProcessor tickProcessor;

    public CsvStream(String inputPath, TickProcessor tickProcessor) {
        this.inputPath = inputPath;
        this.tickProcessor = tickProcessor;
    }

    @Override
    public void start() {
        try (BufferedReader reader = new BufferedReader(new FileReader(inputPath))) {

            String line;
            Random random = new Random();

            long startingTime = System.currentTimeMillis();

            while ((line = reader.readLine()) != null) {
                Tick tick = LineParser.getTickData(line);

                tickProcessor.process(tick);
                //Used for simulation of real-time ticks supplying
//                Thread.sleep(random.nextInt(50));
            }

            System.out.println("Completed. General execution time, ms: " + (System.currentTimeMillis() - startingTime));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
