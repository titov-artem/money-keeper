package com.github.money.keeper.view.contoller.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.money.keeper.model.core.Category;

import java.util.Set;

public class CategoryDto {

    @JsonProperty
    public Long id;

    @JsonProperty
    public String name;

    @JsonProperty
    public Set<String> alternatives;

    @JsonCreator
    public CategoryDto() {
    }

    public CategoryDto(Category category) {
        this.id = category.getId();
        this.name = category.getName();
        this.alternatives = category.getAlternatives();
    }

}
