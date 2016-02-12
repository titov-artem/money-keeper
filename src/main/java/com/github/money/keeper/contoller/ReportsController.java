package com.github.money.keeper.contoller;

import com.github.money.keeper.model.RawTransaction;
import com.github.money.keeper.model.report.PerMonthCategoryChart;
import com.github.money.keeper.model.report.PeriodExpenseReportChart;
import com.github.money.keeper.service.CategoryService;
import com.github.money.keeper.service.StoreService;
import com.github.money.keeper.service.TransactionStoreInjector;
import com.github.money.keeper.storage.TransactionRepo;
import com.github.money.keeper.ui.WebUIHolderProvider;
import org.springframework.beans.factory.annotation.Required;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.time.LocalDate;
import java.util.Map;

/**
 * @author Artem Titov
 */
@Path("/reports")
public class ReportsController {

    private CategoryService categoryService;
    private StoreService storeService;
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

        TransactionStoreInjector storeInjector = storeService.getStoreInjector();

        for (RawTransaction transaction : transactionRepo.load(from, to)) {
            builder.append(storeInjector.injectStore(transaction));
        }
        return builder.build();
    }

    @GET
    @Path("/per-month")
    public PerMonthCategoryChart perMonthCategoryChart(LocalDate from, LocalDate to) {
        PerMonthCategoryChart.Builder builder = new PerMonthCategoryChart.Builder(categoryService.getCategorizationHelper());

        TransactionStoreInjector storeInjector = storeService.getStoreInjector();

        for (RawTransaction transaction : transactionRepo.load(from.withDayOfMonth(1), to.withDayOfMonth(1).plusMonths(1).minusDays(1))) {
            builder.append(storeInjector.injectStore(transaction));
        }
        return builder.build();

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
    public void setStoreService(StoreService storeService) {
        this.storeService = storeService;
    }

    @Required
    public void setTransactionRepo(TransactionRepo transactionRepo) {
        this.transactionRepo = transactionRepo;
    }
}
