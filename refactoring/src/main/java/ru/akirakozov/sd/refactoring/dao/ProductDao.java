package ru.akirakozov.sd.refactoring.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Semaphore;
import java.util.function.Function;

public class ProductDao implements IProductDao {
    private static final String CREATE_PRODUCT_TABLE_SQL = "CREATE TABLE IF NOT EXISTS PRODUCT" +
            "(ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
            " NAME           TEXT    NOT NULL, " +
            " PRICE          INT     NOT NULL)";
    private static final String DROP_PRODUCT_TABLE_SQL = "DROP TABLE IF EXISTS PRODUCT";
    private static ProductDao productDao;

    private Semaphore semaphore = new Semaphore(20);

    private final String jdbcUrl;

    private ProductDao(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    public static ProductDao getInstance(String jdbcUrl) {
        if (productDao != null) {
            return productDao;
        }
        return (productDao = new ProductDao(jdbcUrl));
    }

    @Override
    public void create() {
        withDatabaseConnection(statement -> statement.executeUpdate(CREATE_PRODUCT_TABLE_SQL));
    }

    @Override
    public void drop() {
        withDatabaseConnection(statement -> statement.executeUpdate(DROP_PRODUCT_TABLE_SQL));
    }

    public void add(Product product) {
        withDatabaseConnection(statement ->
                statement.executeUpdate("INSERT INTO PRODUCT (NAME, PRICE) " +
                        "VALUES (\"" + product.getName() + "\", " + product.getPrice() + ")"));
    }

    @Override
    public List<Product> findAll() {
        return queryRows("SELECT * FROM PRODUCT", this::rowToProduct);
    }


    private <T> List<T> queryRows(String query, Function<ResultSet, T> rowMapper) {
        var result = new ArrayList<T>();
        withDatabaseConnection(statement -> {
            try (ResultSet rs = statement.executeQuery(query)) {
                while (rs.next())
                    result.add(rowMapper.apply(rs));
            }
        });
        return result;
    }

    private <T> Optional<T> querySingleRow(String query, Function<ResultSet, T> rowMapper) {
        return queryRows(query, rowMapper).stream().findFirst();
    }

    private Product rowToProduct(ResultSet resultSet) {
        try {
            var name = resultSet.getString("name");
            long price = Long.parseLong(resultSet.getString("price"));
            return new Product(name, price);
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    private Optional<Product> querySingleProductRow(String query) {
        return querySingleRow(query, this::rowToProduct);
    }

    @Override
    public Optional<Product> max() {
        return querySingleProductRow("SELECT * FROM PRODUCT ORDER BY PRICE DESC LIMIT 1");
    }

    @Override
    public Optional<Product> min() {
        return querySingleProductRow("SELECT * FROM PRODUCT ORDER BY PRICE LIMIT 1");
    }

    private long mapToLong(ResultSet resultSet) {
        try {
            return resultSet.getInt(1);
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    @Override
    public long sum() {
        return querySingleRow("SELECT SUM(PRICE) FROM PRODUCT", this::mapToLong).orElse(0L);
    }

    @Override
    public long count() {
        return querySingleRow("SELECT COUNT(*) FROM PRODUCT", this::mapToLong).orElse(0L);
    }

    private void withDatabaseConnection(StatementConsumer action) {
        try {
            semaphore.acquire();
            Connection connection = DriverManager.getConnection(jdbcUrl);
            try (Statement statement = connection.createStatement()) {
                action.accept(statement);
            }
            connection.close();
            semaphore.release();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }
}
