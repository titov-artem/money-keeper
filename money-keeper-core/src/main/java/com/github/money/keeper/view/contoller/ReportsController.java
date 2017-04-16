package com.github.money.keeper.view.contoller;

import com.github.money.keeper.model.core.RawTransaction;
import com.github.money.keeper.model.report.PerMonthCategoryChart;
import com.github.money.keeper.model.report.PeriodExpenseReportChart;
import com.github.money.keeper.service.CategoryService;
import com.github.money.keeper.service.StoreService;
import com.github.money.keeper.service.TransactionStoreInjector;
import com.github.money.keeper.storage.AccountRepo;
import com.github.money.keeper.storage.CategoryRepo;
import com.github.money.keeper.storage.TransactionRepo;
import com.github.money.keeper.view.contoller.dto.PeriodReportForm;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

/**
 * @author Artem Titov
 */
@Controller
@Path("/reports")
public class ReportsController implements REST {

    private final CategoryService categoryService;
    private final StoreService storeService;
    private final TransactionRepo transactionRepo;
    private final AccountRepo accountRepo;
    private final CategoryRepo categoryRepo;

    @Inject
    public ReportsController(CategoryService categoryService,
                             StoreService storeService,
                             TransactionRepo transactionRepo,
                             AccountRepo accountRepo,
                             CategoryRepo categoryRepo) {
        this.categoryService = categoryService;
        this.storeService = storeService;
        this.transactionRepo = transactionRepo;
        this.accountRepo = accountRepo;
        this.categoryRepo = categoryRepo;
    }

    @POST
    @Path("/period")
    public PeriodExpenseReportChart periodExpenseReportChart(PeriodReportForm form) {
        PeriodExpenseReportChart.Builder builder = new PeriodExpenseReportChart.Builder(
                accountRepo,
                categoryRepo,
                form.from,
                form.to
        );


        List<RawTransaction> transactions = form.accountIds == null || form.accountIds.isEmpty()
                ? transactionRepo.load(form.from, form.to)
                : transactionRepo.load(form.from, form.to, form.accountIds);

        TransactionStoreInjector storeInjector = storeService.getStoreInjector(transactions);

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

        List<RawTransaction> transactions = accountIds.isEmpty()
                ? transactionRepo.load(from, to)
                : transactionRepo.load(from, to, accountIds);

        TransactionStoreInjector storeInjector = storeService.getStoreInjector(transactions);
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

}
