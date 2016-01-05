package com.github.money.keeper.serializer.html;

import com.github.money.keeper.model.report.SimpleExpenseReport;
import com.github.money.keeper.template.TemplateSupport;
import com.google.common.collect.ImmutableMap;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Required;

import java.io.StringWriter;
import java.util.Map;

public class SimpleExpenseReportHtmlSerializer {

    private TemplateSupport templateSupport;

    public String serialize(SimpleExpenseReport report) {
        Template template = templateSupport.getTemplate("simple_expense_report.ftl");
        Map<String, Object> root = ImmutableMap.of("report", report);
        StringWriter writer = new StringWriter();
        try {
            template.process(root, writer);
        } catch (Exception e) {
            throw new RuntimeException("Failed to build report due to exception", e);
        }
        return writer.toString();
    }

    @Required
    public void setTemplateSupport(TemplateSupport templateSupport) {
        this.templateSupport = templateSupport;
    }
}
