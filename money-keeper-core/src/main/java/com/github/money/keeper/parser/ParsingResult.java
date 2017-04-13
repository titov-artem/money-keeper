package com.github.money.keeper.parser;

import com.github.money.keeper.model.core.Account;
import com.google.common.collect.ImmutableList;

import java.util.List;

public class ParsingResult {

    private final Account account;
    private final ImmutableList<ParsedTransaction> transactions;

    public ParsingResult(Account account, List<ParsedTransaction> transactions) {
        this.account = account;
        this.transactions = ImmutableList.copyOf(transactions);
    }

    public Account getAccount() {
        return account;
    }

    public ImmutableList<ParsedTransaction> getTransactions() {
        return transactions;
    }

    @Override public String toString() {
        return "ParsingResult{" +
                "account=" + account +
                ", transactions=" + transactions +
                '}';
    }
}
