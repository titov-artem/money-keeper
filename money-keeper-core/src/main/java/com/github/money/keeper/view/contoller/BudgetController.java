package com.github.money.keeper.view.contoller;

import com.github.money.keeper.model.core.Budget;
import com.github.money.keeper.model.service.BudgetStatistics;
import com.github.money.keeper.service.BudgetService;
import com.github.money.keeper.storage.BudgetRepo;
import com.github.money.keeper.view.contoller.dto.BudgetForm;
import com.github.money.keeper.view.contoller.dto.BudgetView;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Controller
@Path("/budget")
public class BudgetController implements REST {

    private final BudgetService budgetService;
    private final BudgetRepo budgetRepo;

    private final Clock clock;

    @Inject
    public BudgetController(BudgetService budgetService,
                            BudgetRepo budgetRepo,
                            Clock clock) {
        this.budgetService = budgetService;
        this.budgetRepo = budgetRepo;
        this.clock = clock;
    }

    @GET
    public List<BudgetView> get() {
        LocalDate now = LocalDate.now(clock);
        List<BudgetStatistics> statistics = budgetService.getBudgetStatistics(now.minusMonths(1), now);
        return statistics.stream()
                .map(BudgetView::new)
                .collect(toList());
    }

    @POST
    public BudgetView create(BudgetForm form) {
        return new BudgetView(budgetService.createBudget(new Budget(
                form.accountIds,
                form.categoryIds,
                form.name,
                form.from,
                form.to,
                form.amount
        )));
    }

}
