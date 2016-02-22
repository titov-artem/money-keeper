package com.github.money.keeper.view.contoller.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.money.keeper.model.Account;
import com.github.money.keeper.parser.ParserType;

public class AccountDto {

    @JsonProperty
    public Long id;

    @JsonProperty
    public String name;

    @JsonProperty
    public ParserType parserType;

    @JsonProperty
    public String parserTypeName;

    @JsonProperty
    public String color;

    @JsonCreator
    public AccountDto() {
    }

    public AccountDto(Account account) {
        this.id = account.getId();
        this.name = account.getName();
        this.parserType = account.getParserType();
        this.parserTypeName = account.getParserType().getSourceName();
        this.color = account.getParserType().getColor();
    }

}
