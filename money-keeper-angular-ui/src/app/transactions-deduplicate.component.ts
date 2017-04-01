import {Component} from "@angular/core";
import {Transaction} from "./model/Transaction";
import {TransactionsService} from "./service/transactions.service";
import {AlertComponent} from "./alert.component";

@Component({
    moduleId: module.id,
    selector: 'transactions-deduplicate',
    templateUrl: './html/transactions-deduplicate.component.html',
})
export class TransactionsDeduplicateComponent {
    transactions: Transaction[] = null;

    private from: string = null;
    private to: string = null;
    private alert: AlertComponent;

    constructor(private transactionsService: TransactionsService) {
    }


    setTransactions(transactions: Transaction[],
                    from: string,
                    to: string,
                    alert: AlertComponent): void {
        this.transactions = transactions;
        this.from = from;
        this.to = to;
        this.alert = alert;
    }

    deduplicateTransactions(): void {
        if (this.transactions != null) {
            this.transactionsService.deduplicateTransactions(
                this.from,
                this.to,
                data => this.alert.showAlert(AlertComponent.SUCCESS, 'Removed ' + data.length + ' duplicates', 3000),
                error => {
                    console.log(error);
                    this.alert.showAlert(AlertComponent.DANGER, 'Failed to remove duplicates', 3000);
                }
            );
            this.clear();
        }
    }

    clear(): void {
        this.transactions = null;
        this.from = null;
        this.to = null;
    }

}