package ru.akirakozov.sd.refactoring.servlet;

import ru.akirakozov.sd.refactoring.dao.IProductDao;
import ru.akirakozov.sd.refactoring.util.HtmlWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author akirakozov
 */
public class GetProductsServlet extends AbstractServlet {
    public GetProductsServlet(IProductDao productDao, HtmlWriter htmlWriter) {
        super(productDao, htmlWriter);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        var products = productDao.findAll();
        htmlWriter.writeResults(response, products, null);
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
