import {Injectable} from "@angular/core";
import {AbstractService} from "./abstract.service";
import {Headers, Http} from "@angular/http";
import {Budget} from "../model/budget";

@Injectable()
export class BudgetService extends AbstractService {

    private headers = new Headers({'Content-Type': 'application/json'});
    private budgetsUrl = '/services/api/budget';

    constructor(private http: Http) {
        super();
    }

    getBudgets(): Promise<Budget[]> {
        let url = `${this.budgetsUrl}?`;
        return this.http.get(url, {headers: this.headers})
            .toPromise()
            .then(res => res.json())
            .catch(this.handleError);
    }

    create(accounts: number[],
           categories: number[],
           name: string,
           from: string,
           to: string,
           amount: number): Promise<Budget> {
        const url = `${this.budgetsUrl}`;
        return this.http.post(url, JSON.stringify({
            accountIds: accounts,
            categoryIds: categories,
            name: name,
            from: from,
            to: to,
            amount: amount
        }), {headers: this.headers})
            .toPromise()
            .then(res => res.json())
            .catch(this.handleError);
    }
}