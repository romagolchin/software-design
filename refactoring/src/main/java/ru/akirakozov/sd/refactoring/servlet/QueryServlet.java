package ru.akirakozov.sd.refactoring.servlet;

import ru.akirakozov.sd.refactoring.dao.IProductDao;
import ru.akirakozov.sd.refactoring.util.HtmlWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author akirakozov
 */
public class QueryServlet extends AbstractServlet {
    public QueryServlet(IProductDao productDao, HtmlWriter htmlWriter) {
        super(productDao, htmlWriter);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        var command = request.getParameter("command");
        if ("max".equals(command)) {
            htmlWriter.writeOptionalResult(response, productDao.max(), "Product with max price: ");
        } else if ("min".equals(command)) {
            htmlWriter.writeOptionalResult(response, productDao.min(), "Product with min price: ");
        } else if ("count".equals(command)) {
            htmlWriter.writeSingleResult(response, productDao.count(), "Number of products: ");
        } else if ("sum".equals(command)) {
            htmlWriter.writeSingleResult(response, productDao.sum(), "Summary price: ");
        } else {
            response.getWriter().println("Unknown command " + command);
        }
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
