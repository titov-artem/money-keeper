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
        this(category.getName(), category.getAlternatives());
    }

    public CategoryDto(String name, Set<String> alternatives) {
        this.name = name;
        this.alternatives = alternatives;
    }
}
