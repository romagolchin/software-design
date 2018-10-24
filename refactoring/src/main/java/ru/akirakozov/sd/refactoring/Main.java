package ru.akirakozov.sd.refactoring;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import ru.akirakozov.sd.refactoring.dao.ProductDao;
import ru.akirakozov.sd.refactoring.servlet.AddProductServlet;
import ru.akirakozov.sd.refactoring.servlet.GetProductsServlet;
import ru.akirakozov.sd.refactoring.servlet.QueryServlet;
import ru.akirakozov.sd.refactoring.util.HtmlWriter;

/**
 * @author akirakozov
 */
public class Main {
    public static void main(String[] args) throws Exception {
        ProductDao dao = ProductDao.getInstance("jdbc:sqlite:test.db");
        HtmlWriter htmlWriter = HtmlWriter.getInstance();
        Server server = new Server(8081);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);

        context.addServlet(new ServletHolder(new AddProductServlet(dao, htmlWriter)), "/add-product");
        context.addServlet(new ServletHolder(new GetProductsServlet(dao, htmlWriter)),"/get-products");
        context.addServlet(new ServletHolder(new QueryServlet(dao, htmlWriter)),"/query");

        server.start();
        server.join();
    }
}
