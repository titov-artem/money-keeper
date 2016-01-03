package com.github.money.keeper.template;

import com.fasterxml.jackson.databind.ObjectMapper;
import freemarker.template.Template;

import java.io.StringWriter;
import java.util.Map;

public class UITemplateSupport extends TemplateSupport {

    private static final String UI_TEMPLATES_SUBPATH = "ui/";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public Template getTemplate(String fileName) {
        return super.getTemplate(UI_TEMPLATES_SUBPATH + fileName);
    }

    public String applyTemplate(String templateFileName, String templateParams) {
        Template template = getTemplate(templateFileName);
        StringWriter writer = new StringWriter();
        try {
            template.process(OBJECT_MAPPER.readValue(templateParams, Map.class), writer);
        } catch (Exception e) {
            throw new RuntimeException("Failed to build report due to exception", e);
        }
        return writer.toString();
    }
}
