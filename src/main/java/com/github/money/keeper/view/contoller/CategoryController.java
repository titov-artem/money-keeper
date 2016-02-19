package com.github.money.keeper.view.contoller;

import com.github.money.keeper.model.Category;
import com.github.money.keeper.service.CategorizationHelper;
import com.github.money.keeper.service.CategoryService;
import com.github.money.keeper.storage.StoreRepo;
import com.github.money.keeper.view.contoller.dto.CategoryDto;
import com.github.money.keeper.view.contoller.dto.ExtendedCategoryDto;
import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import com.google.common.collect.SetMultimap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Required;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@Path("/category")
public class CategoryController {

    private CategoryService categoryService;
    private StoreRepo storeRepo;

    @GET
    public List<ExtendedCategoryDto> getCategories() {
        SetMultimap<Category, String> storeByCategory = getCategoryToStoreName();
        return categoryService.loadAll().stream()
                .map(c -> new ExtendedCategoryDto(c, storeByCategory.get(c)))
                .sorted((o1, o2) -> o1.name.compareTo(o2.name))
                .collect(toList());
    }

    @POST
    @Path("/check/name")
    public boolean checkName(String categoryName, Set<String> affectedNames) {
        return checkNameInternal(categoryName, affectedNames);
    }

    @POST
    @Path("/rename")
    public ExtendedCategoryDto rename(String oldName, String newName) {
        Preconditions.checkArgument(checkNameInternal(newName, ImmutableSet.of(oldName)), "Name must be not empty and unique");
        oldName = StringUtils.strip(oldName);
        newName = StringUtils.strip(newName);

        Category newCategory = categoryService.rename(oldName, newName);
        SetMultimap<Category, String> categoryToStoreName = getCategoryToStoreName();
        return new ExtendedCategoryDto(newCategory, categoryToStoreName.get(newCategory));
    }

    @POST
    @Path("/union")
    public ExtendedCategoryDto union(String name, CategoryDto[] categories) {
        List<CategoryDto> categoriesList = Arrays.asList(categories);
        Preconditions.checkArgument(checkNameInternal(name, categoriesList.stream().map(c -> c.name).collect(toSet())), "Name must be not empty and unique");
        Preconditions.checkArgument(categories.length >= 2, "At least 2 categories can be united");
        name = StringUtils.strip(name);
        Category category = categoryService.union(
                name,
                categoriesList.stream()
                        .map(c -> new Category(c.name, c.alternatives))
                        .collect(toList())
        );
        SetMultimap<Category, String> categoryToStoreName = getCategoryToStoreName();
        return new ExtendedCategoryDto(category, categoryToStoreName.get(category));
    }

    private SetMultimap<Category, String> getCategoryToStoreName() {
        CategorizationHelper categorizationHelper = categoryService.getCategorizationHelper();
        return storeRepo.loadAll().stream()
                .collect(
                        HashMultimap::create,
                        (m, s) -> m.put(categorizationHelper.determineCategory(s), s.getName()),
                        Multimap::putAll
                );
    }

    private boolean checkNameInternal(String categoryName, Set<String> affectedNames) {
        if (StringUtils.isBlank(categoryName)) return false;
        Category category = categoryService.load(categoryName);
        return category == null || affectedNames.contains(categoryName);
    }

    @Required
    public void setCategoryService(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Required
    public void setStoreRepo(StoreRepo storeRepo) {
        this.storeRepo = storeRepo;
    }
}
