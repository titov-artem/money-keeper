package com.github.money.keeper.view.ui.template;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;

import javax.annotation.PostConstruct;
import java.io.IOException;

public class TemplateSupport {

    public static final String TEMPLATES_PATH = "templates";
    private Configuration cfg;

    @PostConstruct
    public void init() throws IOException {
        cfg = new Configuration(Configuration.VERSION_2_3_23);
        cfg.setClassLoaderForTemplateLoading(getClass().getClassLoader(), TEMPLATES_PATH);
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
    }

    public Template getTemplate(String fileName) {
        try {
            return cfg.getTemplate(fileName);
        } catch (IOException e) {
            throw new RuntimeException("Failed to get template " + fileName, e);
        }
    }

}
