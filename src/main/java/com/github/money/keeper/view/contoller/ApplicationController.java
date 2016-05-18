package com.github.money.keeper.view.contoller;

import com.github.money.keeper.view.ui.WebUIHolderProvider;

import javax.ws.rs.POST;
import javax.ws.rs.Path;

/**
 * Managing application menu
 *
 * @author Artem Titov
 */
@Path("/application")
public class ApplicationController {

    @POST
    @Path("/switch")
    public void switchPage(Page page) {
        WebUIHolderProvider.INSTANCE.getWebUIHolder().setPermanentMember("source", page.name());
        WebUIHolderProvider.INSTANCE.getWebUIHolder().switchView(page.htmlFileName);
    }

    public enum Page {
        HOME("ui/html/home/home.html"),
        REPORTS("ui/html/reports/reports.html"),
        TRANSACTIONS("ui/html/transactions/transactions.html"),
        CATEGORIES("ui/html/category/category-editor.html"),
        STORES("ui/html/store/stores-editor.html");

        private final String htmlFileName;

        Page(String htmlFileName) {
            this.htmlFileName = htmlFileName;
        }
    }
}
