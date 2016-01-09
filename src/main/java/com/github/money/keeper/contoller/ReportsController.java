package com.github.money.keeper.contoller;

import com.github.money.keeper.model.RawTransaction;
import com.github.money.keeper.model.SalePoint;
import com.github.money.keeper.model.Store;
import com.github.money.keeper.model.UnifiedTransaction;
import com.github.money.keeper.model.report.PeriodExpenseReportChart;
import com.github.money.keeper.storage.CategoryRepo;
import com.github.money.keeper.storage.StoreRepo;
import com.github.money.keeper.storage.TransactionRepo;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Required;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

/**
 * @author Artem Titov
 */
@Path("/reports")
public class ReportsController {

    private CategoryRepo categoryRepo;
    private StoreRepo storeRepo;
    private TransactionRepo transactionRepo;

    @GET
    @Path("/period")
    public PeriodExpenseReportChart periodExpenseReportChart() {
        PeriodExpenseReportChart.Builder builder = new PeriodExpenseReportChart.Builder(null, categoryRepo.loadAll());

        Map<SalePoint, Store> pointToStore = storeRepo.loadAll().stream()
                .flatMap(s -> s.getSalePoints().stream().map(p -> Pair.of(p, s)))
                .collect(toMap(Pair::getKey, Pair::getValue));

        for (RawTransaction transaction : transactionRepo.loadAll()) {
            builder.append(new UnifiedTransaction(transaction, pointToStore.get(transaction.getSalePoint())));
        }
        return builder.build();
    }

    @Required
    public void setCategoryRepo(CategoryRepo categoryRepo) {
        this.categoryRepo = categoryRepo;
    }

    @Required
    public void setStoreRepo(StoreRepo storeRepo) {
        this.storeRepo = storeRepo;
    }

    @Required
    public void setTransactionRepo(TransactionRepo transactionRepo) {
        this.transactionRepo = transactionRepo;
    }
}
