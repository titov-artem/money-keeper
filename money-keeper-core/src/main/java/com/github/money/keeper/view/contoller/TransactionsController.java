package com.github.money.keeper.view.contoller;

import com.github.money.keeper.model.core.RawTransaction;
import com.github.money.keeper.model.report.UnifiedTransactionReportView;
import com.github.money.keeper.service.StoreService;
import com.github.money.keeper.service.TransactionService;
import com.github.money.keeper.service.TransactionStoreInjector;
import com.github.money.keeper.storage.TransactionRepo;
import org.springframework.stereotype.Controller;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.ws.rs.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;

@Controller
@Path("/transactions")
public class TransactionsController implements REST {

    private final TransactionRepo transactionRepo;
    private final StoreService storeService;
    private final TransactionService transactionService;

    @Inject
    public TransactionsController(TransactionRepo transactionRepo,
                                  StoreService storeService,
                                  TransactionService transactionService) {
        this.transactionRepo = transactionRepo;
        this.storeService = storeService;
        this.transactionService = transactionService;
    }

    @GET
    public List<UnifiedTransactionReportView> getTransactions(@QueryParam("from") @Nullable LocalDate from,
                                                              @QueryParam("to") @Nullable LocalDate to,
                                                              @QueryParam("account_ids") Set<Integer> accountIds) {
        List<RawTransaction> transactions = accountIds.isEmpty()
                ? transactionRepo.load(from, to)
                : transactionRepo.load(from, to, accountIds);
        TransactionStoreInjector storeInjector = storeService.getStoreInjector(transactions);

        return transactions.stream()
                .map(storeInjector::injectStore)
                .map(UnifiedTransactionReportView::new)
                .collect(toList());
    }

    @POST
    @Path("/remove-batch")
    public void deduplicate(Set<Long> transactionIds) {
        transactionRepo.delete(transactionIds);
    }

    @DELETE
    @Path("/{id}")
    public void deleteTransaction(@PathParam("id") long transactionId) {
        transactionRepo.delete(transactionId);
    }

}
