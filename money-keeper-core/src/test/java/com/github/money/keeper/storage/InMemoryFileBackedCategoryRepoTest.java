package com.github.money.keeper.storage;

import com.github.money.keeper.model.Category;
import com.github.money.keeper.storage.memory.InMemoryFileBackedCategoryRepo;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import org.junit.Test;

import java.io.File;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class InMemoryFileBackedCategoryRepoTest {

    @Test
    public void testSave() throws Exception {
        Category category1 = new Category("cat1", ImmutableSet.of("c1", "category1"));
        Category category2 = new Category("cat2", ImmutableSet.of());

        File repoFile = File.createTempFile("category-repo", ".json");
        repoFile.deleteOnExit();

        InMemoryFileBackedCategoryRepo repo1 = new InMemoryFileBackedCategoryRepo();
        repo1.setStorageFileName(repoFile.getAbsolutePath());
        repo1.init();
        repo1.save(Lists.newArrayList(
                category1,
                category2
        ));
        repo1.destroy();

        InMemoryFileBackedCategoryRepo repo2 = new InMemoryFileBackedCategoryRepo();
        repo2.setStorageFileName(repoFile.getAbsolutePath());
        repo2.init();
        List<Category> categories = repo2.loadAll().stream()
                .sorted((o1, o2) -> o1.getName().compareTo(o2.getName()))
                .collect(toList());
        repo2.destroy();

        assertThat(categories.size(), is(2));
        assertThat(categories.get(0), is(category1));
        assertThat(categories.get(1), is(category2));
    }
}