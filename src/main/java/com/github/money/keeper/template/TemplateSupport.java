package com.github.money.keeper.template;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;

public class TemplateSupport {

    public static final String TEMPLATES_PATH = "templates";
    private Configuration cfg;

    @PostConstruct
    public void init() throws IOException {
        cfg = new Configuration(Configuration.VERSION_2_3_23);
        cfg.setDirectoryForTemplateLoading(new File(getClass().getClassLoader().getResource(TEMPLATES_PATH).getFile()));
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
    }

    public Template getTemplate(String fileName) {
        try {
            return cfg.getTemplate(fileName);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load template " + fileName, e);
        }
    }

}
