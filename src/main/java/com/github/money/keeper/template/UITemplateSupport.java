package com.github.money.keeper.template;

import com.fasterxml.jackson.databind.ObjectMapper;
import freemarker.template.Template;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import java.io.StringWriter;
import java.util.Map;

public class UITemplateSupport {
    private static final Logger log = LoggerFactory.getLogger(UITemplateSupport.class);

    private TemplateSupport templateSupport;

    private static final String UI_TEMPLATES_SUBPATH = "ui/";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public String applyTemplate(String templateFileName, String templateParams) {
        try {
            Template template = templateSupport.getTemplate(UI_TEMPLATES_SUBPATH + templateFileName);
            StringWriter writer = new StringWriter();
            template.process(OBJECT_MAPPER.readValue(templateParams, Map.class), writer);
            return writer.toString();
        } catch (Exception e) {
            String message = "Failed to apply template " + templateFileName + " to params " + templateParams + " due to exception";
            log.error(message, e);
            throw new RuntimeException(message, e);
        }
    }

    @Required
    public void setTemplateSupport(TemplateSupport templateSupport) {
        this.templateSupport = templateSupport;
    }
}
