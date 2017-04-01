import {Injectable} from "@angular/core";
import {AbstractService} from "./abstract.service";
import {Headers, Http} from "@angular/http";

@Injectable()
export class TransactionsService extends AbstractService {

    private headers = new Headers({'Content-Type': 'application/json'});
    private transactionsUrl = '/services/api/transactions';

    constructor(private http: Http) {
        super();
    }

    deduplicateTransactions(from: string,
                            to: string,
                            successCallback: (data: any) => void,
                            errorCallback: (data: any) => void) {
        this.http.post(`${this.transactionsUrl}/deduplicate?from=${from}&to=${to}`, {headers: this.headers})
            .toPromise()
            .then(res => successCallback(res.json()))
            .catch(error => {
                errorCallback(error);
                return this.handleError(error);
            })
    }

}