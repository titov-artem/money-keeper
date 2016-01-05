package com.github.money.keeper.template;

import com.fasterxml.jackson.databind.ObjectMapper;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Required;

import java.io.StringWriter;
import java.util.Map;

public class UITemplateSupport {

    private TemplateSupport templateSupport;

    private static final String UI_TEMPLATES_SUBPATH = "ui/";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public String applyTemplate(String templateFileName, String templateParams) {
        Template template = templateSupport.getTemplate(UI_TEMPLATES_SUBPATH + templateFileName);
        StringWriter writer = new StringWriter();
        try {
            template.process(OBJECT_MAPPER.readValue(templateParams, Map.class), writer);
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
