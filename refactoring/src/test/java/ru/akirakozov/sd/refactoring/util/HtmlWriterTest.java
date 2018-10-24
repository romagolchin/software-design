package ru.akirakozov.sd.refactoring.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

class HtmlWriterTest {
    private HtmlWriter htmlWriter = HtmlWriter.getInstance();

    @Mock
    private PrintWriter printWriter;

    @Mock
    private HttpServletResponse response;

    private StringBuilder stringBuilder;

    @BeforeEach
    void setUp() throws IOException {
        MockitoAnnotations.initMocks(this);
        when(response.getWriter()).thenReturn(printWriter);
        stringBuilder = new StringBuilder();
        doAnswer(invocation -> stringBuilder.append((String) invocation.getArgument(0)))
                .when(printWriter).println(anyString());
    }

    @Test
    void writeResults() {
        htmlWriter.writeResults(
                response,
                IntStream.range(0, 3).boxed().collect(Collectors.toList()),
                "qwe");
        assertEquals("<html><body><h1>qwe</h1>0</br>1</br>2</br></body></html>",
                stringBuilder.toString());
    }

    @Test
    void writeSingleResult() {
        htmlWriter.writeSingleResult(response, 0, "rty");
        assertEquals("<html><body><h1>rty</h1>0</br></body></html>", stringBuilder.toString());
    }

    @Test
    void writeOptionalResult() {
        htmlWriter.writeOptionalResult(response, Optional.of(0), "qwe");
        assertEquals("<html><body><h1>qwe</h1>0</br></body></html>", stringBuilder.toString());
    }

    @Test
    void writeOptionalResultEmpty() {
        htmlWriter.writeOptionalResult(response, Optional.empty(), "qwe");
        assertEquals("<html><body><h1>qwe</h1></br></body></html>", stringBuilder.toString());
    }
}