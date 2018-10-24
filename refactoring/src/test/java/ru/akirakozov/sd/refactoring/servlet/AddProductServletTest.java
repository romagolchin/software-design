package ru.akirakozov.sd.refactoring.servlet;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.akirakozov.sd.refactoring.dao.Product;

import java.io.IOException;

import static org.eclipse.jetty.http.HttpStatus.OK_200;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddProductServletTest extends ServletAbstractTest {
    private AddProductServlet servlet = new AddProductServlet(productDao, htmlWriter);

    @Test
    void addProduct() throws IOException {
        doReturn("iphone").when(request).getParameter("name");
        doReturn("1000").when(request).getParameter("price");
        servlet.doGet(request, response);
        verify(response).setStatus(OK_200);
        verify(productDao).add(eq(new Product("iphone", 1000)));
    }
}