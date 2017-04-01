package com.github.money.keeper.parser;

import com.github.money.keeper.model.Account;
import com.github.money.keeper.model.RawTransaction;
import com.google.common.collect.ImmutableList;

import java.util.List;

public class ParsingResult {

    private final Account account;
    private final ImmutableList<RawTransaction> transactions;

    public ParsingResult(Account account, List<RawTransaction> transactions) {
        this.account = account;
        this.transactions = ImmutableList.copyOf(transactions);
    }

    public Account getAccount() {
        return account;
    }

    public ImmutableList<RawTransaction> getTransactions() {
        return transactions;
    }

    @Override public String toString() {
        return "ParsingResult{" +
                "account=" + account +
                ", transactions=" + transactions +
                '}';
    }
}
