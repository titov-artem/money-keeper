package com.github.money.keeper.contoller;

import com.github.money.keeper.model.RawTransaction;
import com.github.money.keeper.model.SalePoint;
import com.github.money.keeper.model.Store;
import com.github.money.keeper.model.UnifiedTransaction;
import com.github.money.keeper.model.report.PerMonthCategoryChart;
import com.github.money.keeper.model.report.PeriodExpenseReportChart;
import com.github.money.keeper.service.CategoryService;
import com.github.money.keeper.storage.StoreRepo;
import com.github.money.keeper.storage.TransactionRepo;
import com.github.money.keeper.ui.WebUIHolderProvider;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Required;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.time.LocalDate;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

/**
 * @author Artem Titov
 */
@Path("/reports")
public class ReportsController {

    private CategoryService categoryService;
    private StoreRepo storeRepo;
    private TransactionRepo transactionRepo;

    @SuppressWarnings("VoidMethodAnnotatedWithGET")
    @GET
    public void switchToReport(Report report, Map<String, String> args) {
        WebUIHolderProvider.INSTANCE.getWebUIHolder().switchView(report.htmlFileName, args);
    }

    @GET
    @Path("/period")
    public PeriodExpenseReportChart periodExpenseReportChart(LocalDate from, LocalDate to) {
        PeriodExpenseReportChart.Builder builder = new PeriodExpenseReportChart.Builder(categoryService.getCategorizationHelper(), from, to);

        Map<SalePoint, Store> pointToStore = getSalePointStoreMap();

        for (RawTransaction transaction : transactionRepo.load(from, to)) {
            builder.append(new UnifiedTransaction(transaction, pointToStore.get(transaction.getSalePoint())));
        }
        return builder.build();
    }

    @GET
    @Path("/per-month")
    public PerMonthCategoryChart perMonthCategoryChart(LocalDate from, LocalDate to) {
        PerMonthCategoryChart.Builder builder = new PerMonthCategoryChart.Builder(categoryService.getCategorizationHelper());

        Map<SalePoint, Store> pointToStore = getSalePointStoreMap();

        for (RawTransaction transaction : transactionRepo.load(from.withDayOfMonth(1), to.withDayOfMonth(1).plusMonths(1).minusDays(1))) {
            builder.append(new UnifiedTransaction(transaction, pointToStore.get(transaction.getSalePoint())));
        }
        return builder.build();

    }

    private Map<SalePoint, Store> getSalePointStoreMap() {
        return storeRepo.loadAll().stream()
                .flatMap(s -> s.getSalePoints().stream().map(p -> Pair.of(p, s)))
                .collect(toMap(Pair::getKey, Pair::getValue));
    }

    public enum Report {
        PERIOD("ui/html/reports/period-expense-report-chart.html"),
        PER_MONTH("ui/html/reports/per-month-category-chart.html");

        private final String htmlFileName;

        Report(String htmlFileName) {
            this.htmlFileName = htmlFileName;
        }
    }

    @Required
    public void setCategoryService(CategoryService categoryService) {
        this.categoryService = categoryService;
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
