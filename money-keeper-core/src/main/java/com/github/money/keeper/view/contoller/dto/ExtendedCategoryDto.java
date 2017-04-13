package com.github.money.keeper.view.contoller.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.money.keeper.model.core.Category;

import java.util.List;

public class ExtendedCategoryDto extends CategoryDto {

    @JsonProperty
    public List<String> stores;

    @JsonCreator
    public ExtendedCategoryDto() {
    }

    public ExtendedCategoryDto(Category category, List<String> stores) {
        super(category);
        this.stores = stores;
    }
}
