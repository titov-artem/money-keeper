import {OnInit, Component} from "@angular/core";
import {Store} from "./model/store";
import {StoresService} from "./service/store.service";
import {ExtendedCategory} from "./model/extended.category";
import {CategoriesService} from "./service/category.service";

@Component({
    moduleId: module.id,
    selector: 'category-editor',
    templateUrl: './html/stores-editor.component.html',
    styleUrls: ['./css/store-editor.component.css']
})
export class StoresEditorComponent implements OnInit {
    stores: Store[];
    storeShowEditForm: boolean[];
    categories: ExtendedCategory[];

    constructor(private storesService: StoresService,
                private categoriesService: CategoriesService) {
    }

    ngOnInit(): void {
        this.storesService.getStores()
            .then(stores => {
                this.stores = stores;
                this.storeShowEditForm = [];
                for (let i = 0; i < this.stores.length; i++) {
                    this.storeShowEditForm.push(false);
                }
            });
        this.categoriesService.getCategories()
            .then(categories => this.categories = categories);
    }

    updateStore(store: Store): Promise<Store> {
        return this.storesService.update(store);
    }

}