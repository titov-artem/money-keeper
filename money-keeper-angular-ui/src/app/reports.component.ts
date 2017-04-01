import {Component, OnInit} from "@angular/core";
import {Account} from "./model/account";
import {AccountService} from "./service/account.service";
import {Router} from "@angular/router";
import moment = require("moment");


@Component({
    moduleId: module.id,
    selector: 'reports',
    templateUrl: './html/reports.component.html',
    styleUrls: []
})
export class ReportsComponent implements OnInit {
    accounts: Account[] = [];

    constructor(private accountService: AccountService,
                private router: Router) {
    }

    ngOnInit(): void {
        this.accountService.getAccounts()
            .then(accounts => {
                this.accounts = accounts;
                $('#period-report-panel').find('.input-group.date').datepicker({
                    format: "dd.mm.yyyy",
                    todayHighlight: true
                });
                $('#per-month-report-panel').find('.input-group.date').datepicker({
                    format: "mm.yyyy",
                    startView: 1,
                    minViewMode: 1,
                    todayHighlight: true
                });
            });
    }

    showPeriodReport(from: string, to: string, accountIds: string[]): void {
        console.log('showing report');
        if (!from || !to || !accountIds) return;
        from = moment(from, 'DD.MM.YYYY').format('YYYY-MM-DD');
        to = moment(to, 'DD.MM.YYYY').format('YYYY-MM-DD');
        this.router.navigate(['/reports/period', from, to, accountIds[0]])
    }
}
