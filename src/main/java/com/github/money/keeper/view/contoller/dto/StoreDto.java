package com.github.money.keeper.view.contoller.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.money.keeper.model.Store;
import com.github.money.keeper.service.CategorizationHelper;

public class StoreDto {

    @JsonProperty
    public String name;

    @JsonProperty
    private String categoryName;

    @JsonCreator
    public StoreDto() {
    }

    public StoreDto(Store store, CategorizationHelper categorizationHelper) {
        this.name = store.getName();
        this.categoryName = categorizationHelper.determineCategory(store).getName();
    }

}