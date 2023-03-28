package com.salatin.input;

import static org.mockito.ArgumentMatchers.any;

import com.salatin.TickProcessor;
import com.salatin.model.Tick;
import com.salatin.util.LineParser;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

class CsvStreamTest {
    private static final String BRENT_ONE_LINE_PATH = "src/test/resources/test-brent-line.csv";
    private static final String EMPTY_FILE_PATH = "src/test/resources/empty-file.csv";

    private final TickProcessor tickProcessor = Mockito.mock(TickProcessor.class);

    @Test
    void startWithBadDataThenProcessAndOutMethodShouldNotInvoke() {

        try (MockedStatic<LineParser> lineParserMock = Mockito.mockStatic(LineParser.class)) {
            lineParserMock.when(() -> LineParser.getTickData(any(String.class))).thenReturn(null);

            CsvStream csvStream = new CsvStream(BRENT_ONE_LINE_PATH, tickProcessor);
            csvStream.start();

            Mockito.verify(tickProcessor, Mockito.times(0)).processAndOut(any());
        }
    }

    @Test
    void startWithValidDataThenProcessAndOutMethodShouldInvokeOneTime() {

        try (MockedStatic<LineParser> lineParserMock = Mockito.mockStatic(LineParser.class)) {
            lineParserMock.when(() -> LineParser.getTickData(any(String.class)))
                .thenReturn(createTickObject());

            CsvStream csvStream = new CsvStream(BRENT_ONE_LINE_PATH, tickProcessor);
            csvStream.start();

            Mockito.verify(tickProcessor, Mockito.times(1)).processAndOut(any());
        }
    }

    @Test
    void startWithEmptyFileThenNotExpectedLineParserGetTickDataInvocation() {

        try (MockedStatic<LineParser> lineParserMock = Mockito.mockStatic(LineParser.class)) {
            CsvStream csvStream = new CsvStream(EMPTY_FILE_PATH, tickProcessor);
            csvStream.start();

            lineParserMock.verifyNoInteractions();
        }
    }

    private Tick createTickObject() {
        return new Tick(
            "BRENT",
            BigDecimal.valueOf(73.222),
            32,
            BigDecimal.valueOf(73.278),
            238
        );
    }
}
