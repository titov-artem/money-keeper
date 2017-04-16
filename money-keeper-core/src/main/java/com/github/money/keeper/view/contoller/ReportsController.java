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
import com.github.money.keeper.view.contoller.dto.ReportForm;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.time.LocalDate;
import java.util.List;

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
    public PeriodExpenseReportChart periodExpenseReportChart(ReportForm form) {
        PeriodExpenseReportChart.Builder builder = new PeriodExpenseReportChart.Builder(
                accountRepo,
                categoryRepo,
                form.from,
                form.to
        );


        List<RawTransaction> transactions = transactionRepo.load(form.from, form.to, form.accountIds);

        TransactionStoreInjector storeInjector = storeService.getStoreInjector(transactions);

        for (RawTransaction transaction : transactions) {
            builder.append(storeInjector.injectStore(transaction));
        }
        return builder.build();
    }

    @POST
    @Path("/per-month")
    public PerMonthCategoryChart perMonthCategoryChart(ReportForm form) {
        LocalDate from = form.from.withDayOfMonth(1);
        LocalDate to = form.to.withDayOfMonth(1).plusMonths(1).minusDays(1);

        PerMonthCategoryChart.Builder builder = new PerMonthCategoryChart.Builder(
                accountRepo,
                categoryRepo
        );

        List<RawTransaction> transactions = transactionRepo.load(from, to, form.accountIds);

        TransactionStoreInjector storeInjector = storeService.getStoreInjector(transactions);
        for (RawTransaction transaction : transactions) {
            builder.append(storeInjector.injectStore(transaction));
        }
        return builder.build();

    }

}
