import {NgModule} from "@angular/core";
import {RouterModule, Routes} from "@angular/router";
import {HomeComponent} from "./home.component";
import {ReportsComponent} from "./reports.component";
import {PeriodReportComponent} from "./period-report.component";
import {CategoryEditorComponent} from "./category-editor.component";
import {StoresEditorComponent} from "./stores-editor.component";

const routes: Routes = [
    {path: '', redirectTo: '/home', pathMatch: 'full'},
    {path: 'home', component: HomeComponent},
    {path: 'reports', component: ReportsComponent},
    {path: 'reports/period/:from/:to/:accountId', component: PeriodReportComponent},
    {path: 'categories', component: CategoryEditorComponent},
    {path: 'stores', component: StoresEditorComponent}
];

@NgModule({
    imports: [RouterModule.forRoot(routes)],
    exports: [RouterModule]
})
export class AppRoutingModule {
}
