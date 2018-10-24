package ru.akirakozov.sd.refactoring.servlet;

import ru.akirakozov.sd.refactoring.dao.IProductDao;
import ru.akirakozov.sd.refactoring.dao.Product;
import ru.akirakozov.sd.refactoring.util.HtmlWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author akirakozov
 */
public class AddProductServlet extends AbstractServlet {
    public AddProductServlet(IProductDao productDao, HtmlWriter htmlWriter) {
        super(productDao, htmlWriter);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String name = request.getParameter("name");
        long price = Long.parseLong(request.getParameter("price"));

        productDao.add(new Product(name, price));
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().println("OK");
    }
}
