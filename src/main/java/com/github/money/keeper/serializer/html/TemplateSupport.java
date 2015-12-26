package com.github.money.keeper.serializer.html;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;

import java.io.File;
import java.io.IOException;

public class TemplateSupport {

    private Configuration cfg;

    public void initTemplateEngine() throws IOException {
        cfg = new Configuration(Configuration.VERSION_2_3_23);
        cfg.setDirectoryForTemplateLoading(new File(getClass().getClassLoader().getResource("templates").getFile()));
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
