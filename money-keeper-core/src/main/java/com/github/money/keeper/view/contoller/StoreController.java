package com.github.money.keeper.view.contoller;

import com.github.money.keeper.service.CategorizationHelper;
import com.github.money.keeper.service.CategoryService;
import com.github.money.keeper.storage.StoreRepo;
import com.github.money.keeper.view.contoller.dto.StoreDto;
import com.google.common.collect.Ordering;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;
import javax.ws.rs.*;
import java.util.List;
import java.util.NoSuchElementException;

import static java.util.Collections.singleton;
import static java.util.stream.Collectors.toList;

@Controller
@Path("/store")
public class StoreController implements REST {

    private final StoreRepo storeRepo;
    private final CategoryService categoryService;

    @Inject
    public StoreController(StoreRepo storeRepo, CategoryService categoryService) {
        this.storeRepo = storeRepo;
        this.categoryService = categoryService;
    }

    @GET
    public List<StoreDto> getStores() {
        CategorizationHelper helper = categoryService.getCategorizationHelper();
        return storeRepo.getAll().stream()
                .map(s -> new StoreDto(s, helper))
                .sorted((s1, s2) -> Ordering.natural().nullsLast().compare(s1.name, s2.name))
                .collect(toList());
    }

    @POST
    @Path("/{name}")
    public StoreDto update(@PathParam("name") String storeName,
                           StoreDto form) {
//        Store store = storeRepo.get(storeName);
//        Category category = categoryService.load(form.categoryName);
//        categoryService.changeCategory(store, category);
//        return new StoreDto(store, s -> category);
        return null;
    }

    @POST
    @Path("/{id}/category/{category_id}")
    public StoreDto changeCategory(@QueryParam("id") Long id,
                                   @QueryParam("category_id") Long categoryId) {
        storeRepo.get(id).orElseThrow(NoSuchElementException::new);
        storeRepo.setCategoryId(singleton(id), categoryId);
        return null;
    }

}
