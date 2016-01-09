package com.github.money.keeper.contoller;

import com.github.money.keeper.ui.WebUIHolder;
import com.google.common.base.Preconditions;

import javax.ws.rs.POST;
import javax.ws.rs.Path;

/**
 * Managing application menu
 *
 * @author Artem Titov
 */
@Path("/application")
public class ApplicationController {

    private WebUIHolder webUIHolder;

    @POST
    @Path("/switch")
    public void switchPage(Page page) {
        Preconditions.checkState(webUIHolder != null,
                "Application controller not initialized! Please inject JavaFX WebUIHolder for page switching");
        webUIHolder.setPermanentMember("source", page.name());
        webUIHolder.switchView(page.htmlFileName);
    }

    public enum Page {
        HOME("ui/html/home/home.html"), CATEGORY("ui/html/category/category-editor.html"), REPORTS("ui/html/reports/period-expense-report-chart.html");

        private final String htmlFileName;

        Page(String htmlFileName) {
            this.htmlFileName = htmlFileName;
        }
    }

    public void setWebUIHolder(WebUIHolder webUIHolder) {
        this.webUIHolder = webUIHolder;
    }
}
