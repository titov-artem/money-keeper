package com.github.money.keeper.view.contoller;

import com.github.money.keeper.model.Category;
import com.github.money.keeper.service.CategorizationHelper;
import com.github.money.keeper.service.CategoryService;
import com.github.money.keeper.storage.StoreRepo;
import com.github.money.keeper.view.contoller.dto.ExtendedCategoryDto;
import com.google.common.base.Preconditions;
import com.google.common.collect.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import javax.ws.rs.*;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;

@Path("/category")
public class CategoryController implements REST {
    private static final Logger log = LoggerFactory.getLogger(CategoryController.class);

    private CategoryService categoryService;
    private StoreRepo storeRepo;

    @GET
    public List<ExtendedCategoryDto> getCategories() {
        SetMultimap<Category, String> storeByCategory = getCategoryToStoreName();
        return categoryService.loadAll().stream()
                .map(c -> new ExtendedCategoryDto(c, storeByCategory.get(c)))
                .sorted(Comparator.comparing(o -> o.name))
                .collect(toList());
    }

    @DELETE
    @Path("/{name}")
    public void delete(@PathParam("name") String name) {
        try {
            categoryService.delete(name);
        } catch (RuntimeException e) {
            log.error("Failed to delete category " + name, e);
            throw new InternalServerErrorException("Failed to delete category " + name);
        }
    }

    @POST
    @Path("/check/name")
    public boolean checkName(@QueryParam("category_name") String categoryName,
                             @QueryParam("affected_names") Set<String> affectedNames) {
        return checkNameInternal(categoryName, affectedNames);
    }

    @POST
    @Path("/rename")
    public ExtendedCategoryDto rename(@QueryParam("old_name") String oldName,
                                      @QueryParam("new_name") String newName) {
        Preconditions.checkArgument(checkNameInternal(newName, ImmutableSet.of(oldName)), "Name must be not empty and unique");
        oldName = StringUtils.strip(oldName);
        newName = StringUtils.strip(newName);

        Category newCategory = categoryService.rename(oldName, newName);
        SetMultimap<Category, String> categoryToStoreName = getCategoryToStoreName();
        return new ExtendedCategoryDto(newCategory, categoryToStoreName.get(newCategory));
    }

    @POST
    @Path("/union")
    public ExtendedCategoryDto union(@QueryParam("name") String name,
                                     @QueryParam("category_names") List<String> categoryNames) {
        List<Category> categories = categoryService.load(categoryNames);
        Preconditions.checkArgument(checkNameInternal(name, Sets.newHashSet(categoryNames)), "Name must be not empty and unique");
        Preconditions.checkArgument(categories.size() >= 2, "At least 2 categories can be united");
        name = StringUtils.strip(name);
        Category category = categoryService.union(name, categories);
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
