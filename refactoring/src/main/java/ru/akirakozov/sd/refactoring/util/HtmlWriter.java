package ru.akirakozov.sd.refactoring.util;

import ru.akirakozov.sd.refactoring.dao.ProductDao;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Collections;
import java.util.Optional;

public class HtmlWriter {
    private static HtmlWriter htmlWriter;

    private HtmlWriter() {
    }

    public static HtmlWriter getInstance() {
        if (htmlWriter != null) {
            return htmlWriter;
        }
        return (htmlWriter = new HtmlWriter());
    }

    public <T> void writeResults(HttpServletResponse response, Iterable<T> results, String heading) {
        try {
            var printWriter = response.getWriter();
            printWriter.println("<html><body>");
            if (heading != null)
                printWriter.println("<h1>" + heading + "</h1>");
            for (Object o : results)
                printWriter.println(o + "</br>");
            printWriter.println("</body></html>");
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void writeSingleResult(HttpServletResponse printWriter, Object result, String heading) {
        writeResults(printWriter, Collections.singleton(result), heading);
    }

    public <T> void writeOptionalResult(HttpServletResponse response, Optional<T> result, String heading) {
        writeSingleResult(response, result.map(Object::toString).orElse(""), heading);
    }
}
