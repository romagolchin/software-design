package ru.akirakozov.sd.refactoring.servlet;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.akirakozov.sd.refactoring.dao.IProductDao;
import ru.akirakozov.sd.refactoring.util.HtmlWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ServletAbstractTest {
    IProductDao productDao = mock(IProductDao.class);
    HtmlWriter htmlWriter = mock(HtmlWriter.class);

    @Mock
    HttpServletRequest request;

    @Mock
    HttpServletResponse response;

    @Mock
    PrintWriter printWriter;

    @BeforeEach
    void setUp() throws IOException {
        MockitoAnnotations.initMocks(this);
        when(response.getWriter()).thenReturn(printWriter);
    }
}
