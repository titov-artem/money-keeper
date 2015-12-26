package com.github.money.keeper.storage;

import com.github.money.keeper.model.Category;

import java.util.List;

public interface CategoryRepo {

    void save(Iterable<Category> categories);

    List<Category> loadAll();

}
