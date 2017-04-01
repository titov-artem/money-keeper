package com.github.money.keeper.view.contoller;

import com.github.money.keeper.model.Category;
import com.github.money.keeper.model.Store;
import com.github.money.keeper.service.CategorizationHelper;
import com.github.money.keeper.service.CategoryService;
import com.github.money.keeper.storage.StoreRepo;
import com.github.money.keeper.view.contoller.dto.StoreDto;
import com.google.common.collect.Ordering;
import org.springframework.beans.factory.annotation.Required;

import javax.ws.rs.*;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Path("/store")
public class StoreController implements REST {

    private StoreRepo storeRepo;
    private CategoryService categoryService;

    @GET
    public List<StoreDto> getStores() {
        CategorizationHelper helper = categoryService.getCategorizationHelper();
        return storeRepo.loadAll().stream()
                .map(s -> new StoreDto(s, helper))
                .sorted((s1, s2) -> Ordering.natural().nullsLast().compare(s1.name, s2.name))
                .collect(toList());
    }

    @POST
    @Path("/{name}")
    public StoreDto update(@PathParam("name") String storeName,
                           StoreDto form) {
        Store store = storeRepo.load(storeName);
        Category category = categoryService.load(form.categoryName);
        categoryService.changeCategory(store, category);
        return new StoreDto(store, s -> category);
    }

    @POST
    @Path("/category")
    public StoreDto changeCategory(@QueryParam("store_name") String storeName,
                                   @QueryParam("category_name") String categoryName) {
        Store store = storeRepo.load(storeName);
        Category category = categoryService.load(categoryName);
        categoryService.changeCategory(store, category);
        return new StoreDto(store, s -> category);
    }

    @Required
    public void setStoreRepo(StoreRepo storeRepo) {
        this.storeRepo = storeRepo;
    }

    @Required
    public void setCategoryService(CategoryService categoryService) {
        this.categoryService = categoryService;
    }
}
