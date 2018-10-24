package ru.akirakozov.sd.refactoring.dao;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static java.util.Optional.empty;
import static org.junit.jupiter.api.Assertions.*;

class ProductDaoTest {
//    private ProductDao productDao = ProductDao.getInstance("jdbc:sqlite::memory:");
    private ProductDao productDao = ProductDao.getInstance("jdbc:sqlite:test1.db");

    private Set<Product> products = new HashSet<>();

    @BeforeEach
    void setUp() {
        productDao.create();
    }

    @AfterEach
    void tearDown() {
        productDao.drop();
    }

    private void populate() {
        for (int i = 0; i < 5; i++) {
            Product product = new Product("" + i, i);
            productDao.add(product);
            products.add(product);
        }
    }

    @Test
    void findAll() {
        populate();
        assertEquals(products, new HashSet<>(productDao.findAll()));
    }

    @Test
    void queries() {
        populate();
        assertEquals(Optional.of(new Product("4", 4)), productDao.max());
        assertEquals(Optional.of(new Product("0", 0)), productDao.min());
        assertEquals(5, productDao.count());
        assertEquals(10, productDao.sum());
    }

    @Test
    void queriesWithEmptyResult() {
        assertEquals(empty(), productDao.max());
        assertEquals(empty(), productDao.min());
    }
}