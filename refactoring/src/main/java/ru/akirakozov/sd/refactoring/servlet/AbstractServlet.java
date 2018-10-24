package ru.akirakozov.sd.refactoring.servlet;

import lombok.AllArgsConstructor;
import ru.akirakozov.sd.refactoring.dao.IProductDao;
import ru.akirakozov.sd.refactoring.util.HtmlWriter;

import javax.servlet.http.HttpServlet;

@AllArgsConstructor
public abstract class AbstractServlet extends HttpServlet {
    protected IProductDao productDao;
    protected HtmlWriter htmlWriter;
}
