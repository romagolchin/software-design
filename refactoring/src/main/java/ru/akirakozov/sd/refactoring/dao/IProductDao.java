package ru.akirakozov.sd.refactoring.dao;

import java.util.List;
import java.util.Optional;

public interface IProductDao {
    void create();

    void drop();

    void add(Product product);

    List<Product> findAll();

    long sum();

    Optional<Product> max();

    Optional<Product> min();

    long count();
}
