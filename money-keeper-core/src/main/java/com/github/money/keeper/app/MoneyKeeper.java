package com.github.money.keeper.app;

import com.github.money.keeper.view.ui.template.UITemplateSupport;
import javafx.application.Application;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class MoneyKeeper extends Application {

    private static final ClassPathXmlApplicationContext CONTEXT = new ClassPathXmlApplicationContext("context/application-ctx.xml");

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(CONTEXT::close));
    }

    private UITemplateSupport uiTemplateSupport;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        CONTEXT.getAutowireCapableBeanFactory().autowireBeanProperties(this, AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, true);
        stage.setTitle("Money keeper");
    }

    @Required
    public void setUiTemplateSupport(UITemplateSupport uiTemplateSupport) {
        this.uiTemplateSupport = uiTemplateSupport;
    }

}
