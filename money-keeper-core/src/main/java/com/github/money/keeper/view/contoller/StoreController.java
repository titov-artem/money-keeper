package com.github.money.keeper.view.contoller;

import com.github.money.keeper.model.core.Category;
import com.github.money.keeper.model.core.Store;
import com.github.money.keeper.storage.CategoryRepo;
import com.github.money.keeper.storage.StoreRepo;
import com.github.money.keeper.view.contoller.dto.StoreDto;
import com.google.common.collect.Ordering;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import static java.util.Collections.singleton;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.*;

@Controller
@Path("/store")
public class StoreController implements REST {

    private final StoreRepo storeRepo;
    private final CategoryRepo categoryRepo;

    @Inject
    public StoreController(StoreRepo storeRepo,
                           CategoryRepo categoryRepo) {
        this.storeRepo = storeRepo;
        this.categoryRepo = categoryRepo;
    }

    @GET
    public List<StoreDto> getStores() {
        List<Store> stores = storeRepo.getAll();
        Set<Long> categoryIds = stores.stream().map(Store::getCategoryId).collect(toSet());
        Map<Long, Category> categoryById = categoryRepo.get(categoryIds).stream()
                .collect(toMap(Category::getId, identity()));
        return stores.stream()
                .map(s -> new StoreDto(s, categoryById.get(s.getCategoryId())))
                .sorted((s1, s2) -> Ordering.natural().nullsLast().compare(s1.name, s2.name))
                .collect(toList());
    }

    @POST
    @Path("/{id}/category/{category_id}")
    public StoreDto changeCategory(@PathParam("id") Long id,
                                   @PathParam("category_id") Long categoryId) {
        storeRepo.get(id).orElseThrow(NoSuchElementException::new);
        storeRepo.setCategoryId(singleton(id), categoryId);
        Store store = storeRepo.get(id).get();
        Category category = categoryRepo.get(store.getCategoryId()).get();
        return new StoreDto(store, category);
    }

}
