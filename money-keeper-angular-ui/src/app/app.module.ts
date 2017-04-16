import {NgModule} from "@angular/core";
import {BrowserModule} from "@angular/platform-browser";
import {FormsModule} from "@angular/forms";
import {HttpModule} from "@angular/http";
import {AppRoutingModule} from "./app-routing.module";
import {AppComponent} from "./app.component";
import {AccountService} from "./service/account.service";
import {HomeComponent} from "./home.component";
import {ReportsComponent} from "./reports.component";
import {PeriodReportComponent} from "./period-report.component";
import {PeriodReportService} from "./service/period.report.service";
import {CategoryEditorComponent} from "./category-editor.component";
import {CategoriesService} from "./service/category.service";
import {StoresEditorComponent} from "./stores-editor.component";
import {StoresService} from "./service/store.service";
import {AlertComponent} from "./alert.component";
import {TransactionsDeduplicateComponent} from "./transactions-deduplicate.component";
import {TransactionsService} from "./service/transactions.service";
import {TransactionsComponent} from "./transactions.component";
import {CategoryUnionComponent} from "./category.union.modal.component";

@NgModule({
    imports: [
        BrowserModule,
        FormsModule,
        HttpModule,
        // InMemoryWebApiModule.forRoot(InMemoryDataService),
        AppRoutingModule
    ],
    declarations: [
        AppComponent,

        HomeComponent,
        ReportsComponent,
        PeriodReportComponent,
        CategoryEditorComponent,
        StoresEditorComponent,
        TransactionsComponent,

        AlertComponent,
        TransactionsDeduplicateComponent,
        CategoryUnionComponent
    ],
    providers: [
        AccountService,
        PeriodReportService,
        CategoriesService,
        StoresService,
        TransactionsService
    ],
    bootstrap: [AppComponent]
})
export class AppModule {
}
