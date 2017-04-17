package com.github.money.keeper.view.contoller.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.money.keeper.model.core.Category;
import com.github.money.keeper.model.core.Store;

public class StoreDto {

    @JsonProperty
    public Long id;

    @JsonProperty
    public String name;

    @JsonProperty
    public CategoryDto category;

    @JsonCreator
    public StoreDto() {
    }

    public StoreDto(Store store, Category category) {
        this.id = store.getId();
        this.name = store.getName();
        this.category = new CategoryDto(category);
    }

}
