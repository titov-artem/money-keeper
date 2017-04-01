package com.github.money.keeper.storage;

import com.github.money.keeper.storage.memory.InMemoryFileBackedManyToOneAssociationRepo;

import javax.annotation.PostConstruct;
import java.io.IOException;

/**
 * @author Artem Titov
 */
public class StoreToCategoryRepo extends InMemoryFileBackedManyToOneAssociationRepo {

    @PostConstruct
    @Override
    public void init() throws IOException {
        setFirstKeyName("storeName");
        setSecondKeyName("categoryName");
        super.init();
    }

}
