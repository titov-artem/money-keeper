package com.github.money.keeper.view.contoller.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.money.keeper.model.Category;

import java.util.Set;

public class ExtendedCategoryDto extends CategoryDto {

    @JsonProperty
    public Set<String> stores;

    @JsonCreator
    public ExtendedCategoryDto() {
    }

    public ExtendedCategoryDto(Category category, Set<String> stores) {
        super(category);
        this.stores = stores;
    }
}
