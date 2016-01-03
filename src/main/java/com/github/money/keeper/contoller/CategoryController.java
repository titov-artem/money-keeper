package com.github.money.keeper.contoller;

import com.github.money.keeper.contoller.dto.CategoryDto;
import com.github.money.keeper.model.Category;
import com.github.money.keeper.storage.CategoryRepo;
import com.google.common.collect.Sets;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.util.List;
import java.util.Set;

import static java.util.Collections.singleton;
import static java.util.stream.Collectors.toList;

@Path("/category")
public class CategoryController {

    private CategoryRepo categoryRepo;

    @GET
    public List<CategoryDto> getCategories() {
        return categoryRepo.loadAll().stream().map(CategoryDto::new).collect(toList());
    }

    @POST
    @Path("/rename")
    public CategoryDto rename(String oldName, String newName) {
        Category oldCategory = categoryRepo.load(oldName);
        categoryRepo.delete(oldName);
        Category newCategory = new Category(newName, oldCategory.getAlternatives());
        categoryRepo.save(singleton(newCategory));
        return new CategoryDto(newCategory);
    }

    @POST
    @Path("/union")
    public CategoryDto union(String name, CategoryDto[] categories) {
        Set<String> alternatives = Sets.newHashSet();
        for (CategoryDto c : categories) {
            categoryRepo.delete(c.name);
            alternatives.addAll(c.alternatives);
        }
        Category category = new Category(name, alternatives);
        categoryRepo.save(singleton(category));
        return new CategoryDto(category);
    }

    public void setCategoryRepo(CategoryRepo categoryRepo) {
        this.categoryRepo = categoryRepo;
    }
}
