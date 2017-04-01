import {Injectable} from "@angular/core";
import {Headers, Http} from "@angular/http";
import {ExtendedCategory} from "../model/extended.category";
import {AbstractService} from "./abstract.service";

@Injectable()
export class CategoriesService extends AbstractService {

    private headers = new Headers({'Content-Type': 'application/json'});
    private categoriesUrl = '/services/api/category';  // URL to web api

    constructor(private http: Http) {
        super();
    }

    getCategories(): Promise<ExtendedCategory[]> {
        return this.http.get(this.categoriesUrl, {headers: this.headers})
            .toPromise()
            .then(response => {
                return response.json() as ExtendedCategory[];
            })
            .catch(this.handleError);
    }

    delete(name: string): Promise<void> {
        const url = `${this.categoriesUrl}/${name}`;
        return this.http.delete(url, {headers: this.headers})
            .toPromise()
            .then(() => null)
            .catch(this.handleError);
    }
}