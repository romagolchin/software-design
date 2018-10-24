package ru.akirakozov.sd.refactoring.dao;

import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class Product {
    private String name;

    private long price;

    @Override
    public String toString() {
        return name + "\t" + price;
    }
}
