package com.github.money.keeper.view.contoller;

import com.github.money.keeper.model.Account;
import com.github.money.keeper.storage.AccountRepo;
import com.github.money.keeper.view.contoller.dto.AccountDto;
import org.springframework.beans.factory.annotation.Required;

import javax.ws.rs.*;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Path("/account")
public class AccountController implements REST {

    private AccountRepo accountRepo;

    @GET
    public List<AccountDto> getAccounts() {
        return accountRepo.loadAll().stream().map(AccountDto::new).collect(toList());
    }

    @Path("/{id}")
    @GET
    public AccountDto getAccount(@PathParam("id") Integer id) {
        return Optional.ofNullable(accountRepo.load(id))
                .map(AccountDto::new)
                .orElseThrow(NotFoundException::new);
    }

    @POST
    public AccountDto create(AccountDto dto) {
        Account account = new Account(dto.name, dto.parserType);
        return accountRepo.save(Collections.singleton(account)).stream()
                .map(AccountDto::new)
                .findFirst().get();
    }

    @DELETE
    @Path("/{id}")
    public void delete(@PathParam("id") Integer accountId) {
        accountRepo.delete(accountId);
        // todo delete all transactions for this account
    }

    @Required
    public void setAccountRepo(AccountRepo accountRepo) {
        this.accountRepo = accountRepo;
    }
}
