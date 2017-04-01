import {Component, OnInit, OnDestroy} from "@angular/core";
import {Account} from "./model/account";
import {AccountService} from "./service/account.service";
import {ActivatedRoute} from "@angular/router";
import {PeriodReportService} from "./service/period.report.service";
import {PeriodReport} from "./model/period.report";

declare var c3: any;

@Component({
    moduleId: module.id,
    selector: 'reports',
    templateUrl: './html/period-report.component.html',
    styleUrls: []
})
export class PeriodReportComponent implements OnInit, OnDestroy {
    accounts: Account[] = [];
    report: PeriodReport;
    private from: string;
    private to: string;
    private accountId: string;

    private sub: any;

    constructor(private reportService: PeriodReportService,
                private accountService: AccountService,
                private route: ActivatedRoute) {
    }

    ngOnInit(): void {
        this.sub = this.route.params.subscribe(params => {
            this.from = params['from'];
            this.to = params['to'];
            this.accountId = params['accountId'];
        });
        this.accountService.getAccounts()
            .then(accounts => this.accounts = accounts);
        this.reportService.getReport(this.from, this.to, [this.accountId])
            .then(report => {
                this.report = report;
                this.showReport();
            });
    }

    showReport(): void {
        var charData: any = [];
        var categoryCache: any = [];
        this.report.categoryReports.forEach(function (cReport) {
            charData.push([cReport.category, cReport.amount]);
            categoryCache[cReport.category] = cReport.id;
        });
        var chart = c3.generate({
            bindto: '#chart',
            data: {
                columns: charData,
                type: 'donut',
                onclick: function (e: any) {
                    location.hash = "#" + 'link-' + categoryCache[e.name];
                }
            },
            legend: {
                position: 'right'
            }
        });
    }

    ngOnDestroy(): void {
        this.sub.unsubscribe();
    }
}
