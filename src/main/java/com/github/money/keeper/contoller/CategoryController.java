package com.github.money.keeper.contoller;

import com.github.money.keeper.contoller.dto.CategoryDto;
import com.github.money.keeper.model.Category;
import com.github.money.keeper.storage.CategoryRepo;
import com.google.common.collect.Sets;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.util.List;

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
    public void union(String name, CategoryDto c1, CategoryDto c2) {
        categoryRepo.delete(c1.name);
        categoryRepo.delete(c2.name);
        categoryRepo.save(singleton(new Category(name, Sets.union(c1.alternatives, c2.alternatives))));
    }

    public void setCategoryRepo(CategoryRepo categoryRepo) {
        this.categoryRepo = categoryRepo;
    }
}
