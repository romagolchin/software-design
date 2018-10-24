package ru.akirakozov.sd.refactoring.servlet;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ru.akirakozov.sd.refactoring.dao.IProductDao;
import ru.akirakozov.sd.refactoring.util.HtmlWriter;

import java.io.IOException;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class QueryServletTest extends ServletAbstractTest {
    private QueryServlet servlet = new QueryServlet(productDao, htmlWriter);

    private static Stream<Arguments> commands() {
        return Stream.of(
                arguments(
                        "min",
                        (Consumer<IProductDao>) IProductDao::min,
                        (Consumer<HtmlWriter>) hw -> hw.writeOptionalResult(any(), any(), any())),
                arguments(
                        "max",
                        (Consumer<IProductDao>) IProductDao::max,
                        (Consumer<HtmlWriter>) hw -> hw.writeOptionalResult(any(), any(), any())),
                arguments(
                        "count",
                        (Consumer<IProductDao>) IProductDao::count,
                        (Consumer<HtmlWriter>) hw -> hw.writeSingleResult(any(), any(), any())),
                arguments(
                        "sum",
                        (Consumer<IProductDao>) IProductDao::sum,
                        (Consumer<HtmlWriter>) hw -> hw.writeSingleResult(any(), any(), any())));
    }

    @ParameterizedTest
    @MethodSource("commands")
    void queryProducts(String command, Consumer<IProductDao> operation, Consumer<HtmlWriter> write) throws IOException {
        when(request.getParameter("command")).thenReturn(command);
        servlet.doGet(request, response);
        operation.accept(verify(productDao));
        write.accept(verify(htmlWriter));
    }

    @Test
    void unknownCommand() throws IOException {
        when(request.getParameter("command")).thenReturn("");
        servlet.doGet(request, response);
        verify(printWriter).println(contains("Unknown command"));
    }
}