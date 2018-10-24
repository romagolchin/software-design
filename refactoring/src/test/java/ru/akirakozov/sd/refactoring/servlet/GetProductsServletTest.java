package ru.akirakozov.sd.refactoring.servlet;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

class GetProductsServletTest extends ServletAbstractTest {
    private GetProductsServlet servlet = new GetProductsServlet(productDao, htmlWriter);

    @Test
    void getProducts() throws IOException {
        servlet.doGet(request, response);
        verify(productDao).findAll();
        verify(htmlWriter).writeResults(any(), any(), any());
    }
}