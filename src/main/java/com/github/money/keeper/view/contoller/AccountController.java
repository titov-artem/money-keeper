package com.github.money.keeper.view.contoller;

import com.github.money.keeper.model.Account;
import com.github.money.keeper.storage.AccountRepo;
import com.github.money.keeper.view.contoller.dto.AccountDto;
import org.springframework.beans.factory.annotation.Required;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Path("/account")
public class AccountController {

    private AccountRepo accountRepo;

    @GET
    public List<AccountDto> getAccounts() {
        return accountRepo.loadAll().stream().map(AccountDto::new).collect(toList());
    }

    @POST
    public AccountDto create(AccountDto dto) {
        Account account = new Account(dto.name, dto.parserType);
        return accountRepo.save(Collections.singleton(account)).stream()
                .map(AccountDto::new)
                .findFirst().get();
    }

    @DELETE
    public void delete(Integer accountId) {
        accountRepo.delete(accountId);
        // todo delete all transactions for this account
    }

    @Required
    public void setAccountRepo(AccountRepo accountRepo) {
        this.accountRepo = accountRepo;
    }
}
