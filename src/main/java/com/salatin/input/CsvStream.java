package com.salatin.input;

import com.salatin.Tick;
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

            while ((line = reader.readLine()) != null) {
                Tick tick = LineParser.getTickData(line);

                tickProcessor.process(tick);

                Thread.sleep(random.nextInt(50));
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
