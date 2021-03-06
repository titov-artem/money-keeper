package com.github.money.keeper.view.contoller;

import com.github.money.keeper.model.core.Category;
import com.github.money.keeper.model.core.Store;
import com.github.money.keeper.service.CategoryService;
import com.github.money.keeper.storage.CategoryRepo;
import com.github.money.keeper.storage.StoreRepo;
import com.github.money.keeper.view.contoller.dto.CategoryDto;
import com.github.money.keeper.view.contoller.dto.CategoryUnionForm;
import com.github.money.keeper.view.contoller.dto.ExtendedCategoryDto;
import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;
import javax.ws.rs.*;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

@Controller
@Path("/category")
public class CategoryController implements REST {
    private static final Logger log = LoggerFactory.getLogger(CategoryController.class);

    private final CategoryService categoryService;
    private final CategoryRepo categoryRepo;
    private final StoreRepo storeRepo;

    @Inject
    public CategoryController(CategoryService categoryService,
                              CategoryRepo categoryRepo,
                              StoreRepo storeRepo) {
        this.categoryService = categoryService;
        this.categoryRepo = categoryRepo;
        this.storeRepo = storeRepo;
    }

    @GET
    public List<ExtendedCategoryDto> getCategories() {
        Map<Long, List<Store>> storesByCategoryId = storeRepo.getAllByCategories();
        return categoryRepo.getAll().stream()
                .map(c -> {
                    List<String> storeNames = Optional.ofNullable(storesByCategoryId.get(c.getId()))
                            .map(stores -> stores.stream().map(Store::getName).collect(toList()))
                            .orElse(emptyList());
                    return new ExtendedCategoryDto(c, storeNames);
                })
                .sorted(Comparator.comparing(o -> o.name))
                .collect(toList());
    }

    @POST
    public ExtendedCategoryDto createCategory(CategoryDto dto) {
        Preconditions.checkArgument(StringUtils.isNotBlank(dto.name));
        if (categoryRepo.findByName(dto.name).isPresent()) {
            throw new IllegalArgumentException("Category with such name already exists");
        }
        return new ExtendedCategoryDto(categoryRepo.save(new Category(dto.name, dto.alternatives)), emptyList());
    }

    @DELETE
    @Path("/{id}")
    public void delete(@PathParam("id") Long id) {
        try {
            categoryService.delete(id);
        } catch (RuntimeException e) {
            log.error("Failed to delete category with id " + id, e);
            throw new InternalServerErrorException("Failed to delete category with id " + id);
        }
    }

    @POST
    @Path("/{id}/rename/{name}")
    public ExtendedCategoryDto rename(@PathParam("id") Long categoryId,
                                      @PathParam("name") String name) {
        name = StringUtils.strip(name);
        Preconditions.checkArgument(StringUtils.isNotBlank(name));
        Category category = categoryService.rename(categoryId, name);
        return new ExtendedCategoryDto(category, getCategoryStores(category));
    }

    @POST
    @Path("/union")
    public ExtendedCategoryDto union(CategoryUnionForm form) {
        Preconditions.checkArgument(form.categoryIds.size() >= 2, "At least 2 categories can be united");
        String name = StringUtils.strip(form.name);
        Preconditions.checkArgument(StringUtils.isNotBlank(name));
        Category category = categoryService.union(name, form.categoryIds);
        return new ExtendedCategoryDto(category, getCategoryStores(category));
    }

    private List<String> getCategoryStores(Category category) {
        return storeRepo.findAllByCategory(category.getId()).stream().map(Store::getName).collect(toList());
    }
}
