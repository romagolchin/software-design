package ru.akirakozov.sd.refactoring.dao;

import java.sql.SQLException;
import java.sql.Statement;

public interface StatementConsumer {
    void accept(Statement statement) throws SQLException;
}
