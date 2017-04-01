package com.github.money.keeper.view.contoller;

import com.github.money.keeper.model.RawTransaction;
import com.github.money.keeper.model.UnifiedTransaction;
import com.github.money.keeper.model.report.UnifiedTransactionReportView;
import com.github.money.keeper.service.StoreService;
import com.github.money.keeper.service.TransactionService;
import com.github.money.keeper.service.TransactionStoreInjector;
import com.github.money.keeper.storage.TransactionRepo;
import org.springframework.beans.factory.annotation.Required;

import javax.annotation.Nullable;
import javax.ws.rs.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;

@Path("/transactions")
public class TransactionsController implements REST {

    private TransactionRepo transactionRepo;
    private StoreService storeService;
    private TransactionService transactionService;

    @GET
    public List<UnifiedTransactionReportView> getTransactions(@QueryParam("from") @Nullable LocalDate from,
                                                              @QueryParam("to") @Nullable LocalDate to,
                                                              @QueryParam("account_ids") Set<Integer> accountIds) {
        from = from == null ? LocalDate.MIN : from;
        to = to == null ? LocalDate.MAX : to;

        List<RawTransaction> transactions = accountIds.isEmpty()
                ? transactionRepo.load(from, to)
                : transactionRepo.load(from, to, accountIds);
        TransactionStoreInjector storeInjector = storeService.getStoreInjector();

        return transactions.stream()
                .map(storeInjector::injectStore)
                .map(UnifiedTransactionReportView::new)
                .collect(toList());
    }

    @POST
    @Path("/deduplicate")
    public List<UnifiedTransactionReportView> deduplicate(@QueryParam("from") LocalDate from,
                                                          @QueryParam("to") LocalDate to) {
        List<UnifiedTransaction> removed = transactionService.deduplicate(from, to);
        return removed.stream().map(UnifiedTransactionReportView::new).collect(toList());
    }

    @DELETE
    @Path("/{id}")
    public void deleteTransaction(@PathParam("id") long transactionId) {
        transactionRepo.delete(transactionId);
    }

    @Required
    public void setTransactionRepo(TransactionRepo transactionRepo) {
        this.transactionRepo = transactionRepo;
    }

    @Required
    public void setStoreService(StoreService storeService) {
        this.storeService = storeService;
    }

    @Required
    public void setTransactionService(TransactionService transactionService) {
        this.transactionService = transactionService;
    }
}
