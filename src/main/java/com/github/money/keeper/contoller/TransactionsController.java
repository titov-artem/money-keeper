package com.github.money.keeper.contoller;

import com.github.money.keeper.model.RawTransaction;
import com.github.money.keeper.model.report.UnifiedTransactionReportView;
import com.github.money.keeper.service.StoreService;
import com.github.money.keeper.service.TransactionStoreInjector;
import com.github.money.keeper.storage.TransactionRepo;
import org.springframework.beans.factory.annotation.Required;

import javax.annotation.Nullable;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.time.LocalDate;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Path("/transactions")
public class TransactionsController {

    private TransactionRepo transactionRepo;
    private StoreService storeService;

    @GET
    public List<UnifiedTransactionReportView> getTransactions(@Nullable LocalDate from, @Nullable LocalDate to) {
        from = from == null ? LocalDate.MIN : from;
        to = to == null ? LocalDate.MAX : to;

        List<RawTransaction> transactions = transactionRepo.load(from, to);
        TransactionStoreInjector storeInjector = storeService.getStoreInjector();

        return transactions.stream()
                .map(storeInjector::injectStore)
                .map(UnifiedTransactionReportView::new)
                .collect(toList());
    }

    @Required
    public void setTransactionRepo(TransactionRepo transactionRepo) {
        this.transactionRepo = transactionRepo;
    }

    @Required
    public void setStoreService(StoreService storeService) {
        this.storeService = storeService;
    }

}
