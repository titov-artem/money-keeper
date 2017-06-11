import {AfterViewChecked, Component, ElementRef, OnInit} from "@angular/core";
import {AccountService} from "./service/account.service";
import {Account} from "./model/account";
import {AlertComponent} from "./alert.component";
import {Budget} from "./model/budget";
import {BudgetService} from "./service/budget.service";
import {CategoryService} from "./service/category.service";
import {ExtendedCategory} from "./model/extended.category";
import {isNumeric} from "rxjs/util/isNumeric";
import {Gradation} from "./progress.component";
import moment = require("moment");

@Component({
    moduleId: module.id,
    selector: 'budgets',
    templateUrl: './html/budgets.component.html',
    styleUrls: ['./css/budgets.component.css']
})
export class BudgetsComponent implements OnInit, AfterViewChecked {
    budgets: Budget[];
    accounts: Account[];
    categories: ExtendedCategory[];

    gradation: Gradation = new Gradation(0, 100, 100);
    showCreateForm: boolean = false;

    constructor(private budgetService: BudgetService,
                private accountService: AccountService,
                private categoryService: CategoryService) {
    }

    ngOnInit(): void {
        this.budgetService.getBudgets()
            .then(budgets => this.budgets = budgets);
        this.accountService.getAccounts()
            .then(accounts => this.accounts = accounts);
        this.categoryService.getCategories()
            .then(categories => this.categories = categories);
    }

    ngAfterViewChecked(): void {
        if (this.accounts != null && this.categories != null) {
            $('.selectpicker').selectpicker({
                style: 'btn-info',
                size: 4
            });
        }
        if (this.showCreateForm) {
            $('.input-group.date').datepicker({
                format: "dd.mm.yyyy",
                todayHighlight: true
            });
        }
    }

    createBudget(name: string,
                 from: string,
                 to: string,
                 rawAmount: string,
                 accountIdsSelect: ElementRef,
                 categoryIdsSelect: ElementRef,
                 alert: AlertComponent): void {
        if (name == "") {
            alert.showAlert(AlertComponent.DANGER, 'Name must be specified', 3000);
            return;
        }
        if (from == "") {
            alert.showAlert(AlertComponent.DANGER, 'From must be specified', 3000);
            return;
        }
        if (to == "") {
            alert.showAlert(AlertComponent.DANGER, 'To must be specified', 3000);
            return;
        }
        if (rawAmount == "") {
            alert.showAlert(AlertComponent.DANGER, 'Amount must be specified', 3000);
            return;
        }
        if (!isNumeric(rawAmount)) {
            alert.showAlert(AlertComponent.DANGER, 'Amount must be a number', 3000);
            return;
        }
        let accountIds = $(accountIdsSelect).val();
        if (accountIds == null || accountIds.length == 0) {
            alert.showAlert(AlertComponent.DANGER, 'Select at least one account', 3000);
            return;
        }
        let categoryIds = $(categoryIdsSelect).val();
        if (categoryIds == null || categoryIds.length == 0) {
            alert.showAlert(AlertComponent.DANGER, 'Select at least one category', 3000);
            return;
        }

        to = moment(to, 'DD.MM.YYYY').format('YYYY-MM-DD');
        from = moment(from, 'DD.MM.YYYY').format('YYYY-MM-DD');
        let amount = +rawAmount;
        this.budgetService.create(accountIds, categoryIds, name, from, to, amount)
            .then(budget => {
                this.budgets.push(budget);
                this.showCreateForm = false;
            });
    }
}