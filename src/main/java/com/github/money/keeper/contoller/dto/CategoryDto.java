package com.github.money.keeper.contoller.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.money.keeper.model.Category;

import java.util.Set;

public class CategoryDto {

    @JsonProperty
    public String name;
    @JsonProperty
    public Set<String> alternatives;

    @JsonCreator
    public CategoryDto() {
    }

    public CategoryDto(Category category) {
        this.name = category.getName();
        alternatives = category.getAlternatives();
    }

}
