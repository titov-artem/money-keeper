package com.github.money.keeper.view.contoller;

import com.github.money.keeper.model.RawTransaction;
import com.github.money.keeper.model.report.PerMonthCategoryChart;
import com.github.money.keeper.model.report.PeriodExpenseReportChart;
import com.github.money.keeper.service.CategoryService;
import com.github.money.keeper.service.StoreService;
import com.github.money.keeper.service.TransactionStoreInjector;
import com.github.money.keeper.storage.AccountRepo;
import com.github.money.keeper.storage.TransactionRepo;
import com.github.money.keeper.view.contoller.dto.PeriodReportForm;
import com.github.money.keeper.view.ui.WebUIHolderProvider;
import org.springframework.beans.factory.annotation.Required;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Artem Titov
 */
@Path("/reports")
public class ReportsController implements REST {

    private CategoryService categoryService;
    private StoreService storeService;
    private TransactionRepo transactionRepo;
    private AccountRepo accountRepo;

    @SuppressWarnings("VoidMethodAnnotatedWithGET")
    @GET
    public void switchToReport(Report report, Map<String, String> args) {
        WebUIHolderProvider.INSTANCE.getWebUIHolder().switchView(report.htmlFileName, args);
    }

    @POST
    @Path("/period")
    public PeriodExpenseReportChart periodExpenseReportChart(PeriodReportForm form) {
        PeriodExpenseReportChart.Builder builder = new PeriodExpenseReportChart.Builder(
                categoryService.getCategorizationHelper(),
                accountRepo,
                form.from,
                form.to
        );

        TransactionStoreInjector storeInjector = storeService.getStoreInjector();

        List<RawTransaction> transactions = form.accountIds.isEmpty()
                ? transactionRepo.load(form.from, form.to)
                : transactionRepo.load(form.from, form.to, form.accountIds);
        for (RawTransaction transaction : transactions) {
            builder.append(storeInjector.injectStore(transaction));
        }
        return builder.build();
    }

    @GET
    @Path("/per-month")
    public PerMonthCategoryChart perMonthCategoryChart(@QueryParam("from") LocalDate from,
                                                       @QueryParam("to") LocalDate to,
                                                       @QueryParam("account_ids") Set<Integer> accountIds) {
        from = from.withDayOfMonth(1);
        to = to.withDayOfMonth(1).plusMonths(1).minusDays(1);

        PerMonthCategoryChart.Builder builder = new PerMonthCategoryChart.Builder(
                categoryService.getCategorizationHelper(),
                accountRepo
        );

        TransactionStoreInjector storeInjector = storeService.getStoreInjector();

        List<RawTransaction> transactions = accountIds.isEmpty()
                ? transactionRepo.load(from, to)
                : transactionRepo.load(from, to, accountIds);
        for (RawTransaction transaction : transactions) {
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

    @Required
    public void setAccountRepo(AccountRepo accountRepo) {
        this.accountRepo = accountRepo;
    }
}
