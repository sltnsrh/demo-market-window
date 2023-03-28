package com.salatin.input;

import com.salatin.model.Tick;
import com.salatin.TickProcessor;
import com.salatin.util.LineParser;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CsvStream implements DataStream {
    private static final Logger logger = Logger.getLogger(CsvStream.class.getName());

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

                if (tick != null) {
                    tickProcessor.processAndOut(tick);
                }
                //Used for simulation of real-time ticks supplying
                Thread.sleep(random.nextInt(50));
            }

            logger.log(Level.INFO,
                () -> "File processing has been completed. File path: " + inputPath
                    + ". General execution time, ms: "
                    + (System.currentTimeMillis() - startingTime));

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
