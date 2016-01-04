package com.github.money.keeper.contoller;

import com.github.money.keeper.contoller.dto.CategoryDto;
import com.github.money.keeper.model.Category;
import com.github.money.keeper.storage.CategoryRepo;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static java.util.Collections.singleton;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@Path("/category")
public class CategoryController {

    private CategoryRepo categoryRepo;

    @GET
    public List<CategoryDto> getCategories() {
        return categoryRepo.loadAll().stream().map(CategoryDto::new).collect(toList());
    }

    @POST
    @Path("/check/name")
    public boolean checkName(String categoryName, Set<String> affectedNames) {
        return checkNameInternal(categoryName, affectedNames);
    }

    @POST
    @Path("/rename")
    public CategoryDto rename(String oldName, String newName) {
        Preconditions.checkArgument(checkNameInternal(newName, ImmutableSet.of(oldName)), "Name must be not empty and unique");
        oldName = StringUtils.strip(oldName);
        newName = StringUtils.strip(newName);

        Category oldCategory = categoryRepo.load(oldName);
        categoryRepo.delete(oldName);
        Category newCategory = new Category(newName, oldCategory.getAlternatives());
        categoryRepo.save(singleton(newCategory));
        return new CategoryDto(newCategory);
    }

    @POST
    @Path("/union")
    public CategoryDto union(String name, CategoryDto[] categories) {
        Preconditions.checkArgument(checkNameInternal(name, Arrays.asList(categories).stream().map(c -> c.name).collect(toSet())), "Name must be not empty and unique");
        Preconditions.checkArgument(categories.length >= 2, "At least 2 categories can be united");
        name = StringUtils.strip(name);
        Set<String> alternatives = Sets.newHashSet();
        for (CategoryDto c : categories) {
            categoryRepo.delete(c.name);
            alternatives.addAll(c.alternatives);
        }
        Category category = new Category(name, alternatives);
        categoryRepo.save(singleton(category));
        return new CategoryDto(category);
    }

    private boolean checkNameInternal(String categoryName, Set<String> affectedNames) {
        if (StringUtils.isBlank(categoryName)) return false;
        Category category = categoryRepo.load(categoryName);
        return category == null || affectedNames.contains(categoryName);
    }

    public void setCategoryRepo(CategoryRepo categoryRepo) {
        this.categoryRepo = categoryRepo;
    }
}
