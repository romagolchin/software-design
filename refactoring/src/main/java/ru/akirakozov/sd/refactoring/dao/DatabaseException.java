package ru.akirakozov.sd.refactoring.dao;

public class DatabaseException extends RuntimeException {
    public DatabaseException(Throwable cause) {
        super(cause);
    }
}
