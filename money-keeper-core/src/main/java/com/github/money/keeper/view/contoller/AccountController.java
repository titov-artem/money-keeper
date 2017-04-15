package com.github.money.keeper.view.contoller;

import com.github.money.keeper.model.core.Account;
import com.github.money.keeper.storage.AccountRepo;
import com.github.money.keeper.view.contoller.dto.AccountDto;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;
import javax.ws.rs.*;
import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Controller
@Path("/account")
public class AccountController implements REST {

    private final AccountRepo accountRepo;

    @Inject
    public AccountController(AccountRepo accountRepo) {
        this.accountRepo = accountRepo;
    }

    @GET
    public List<AccountDto> getAccounts() {
        return accountRepo.getAll().stream().map(AccountDto::new).collect(toList());
    }

    @Path("/{id}")
    @GET
    public AccountDto getAccount(@PathParam("id") Long id) {
        return accountRepo.get(id)
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
    public void delete(@PathParam("id") Long accountId) {
        accountRepo.delete(accountId);
        // todo delete all transactions for this account
    }

}
