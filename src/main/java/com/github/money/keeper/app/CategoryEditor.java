package com.github.money.keeper.app;

import com.github.money.keeper.contoller.ApplicationController;
import com.github.money.keeper.template.UITemplateSupport;
import com.github.money.keeper.ui.Endpoint;
import com.github.money.keeper.ui.WebUIHolder;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class CategoryEditor extends Application {

    // todo switch on property
    private static final String categoryEditorFXMLFile = "ui/html/category/category-editor.html";
    private static final ClassPathXmlApplicationContext CONTEXT = new ClassPathXmlApplicationContext("context/application-context.xml");

    private Endpoint endpoint;
    private UITemplateSupport uiTemplateSupport;
    private ApplicationController applicationController;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        CONTEXT.getAutowireCapableBeanFactory().autowireBeanProperties(this, AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, true);
        stage.setTitle("Money keeper");
        WebUIHolder uiHolder = new WebUIHolder(
                800,
                500,
                endpoint,
                uiTemplateSupport
        );
        applicationController.setWebUIHolder(uiHolder);
        Scene scene = new Scene(uiHolder);
        stage.setScene(scene);
        stage.show();
        applicationController.switchPage(ApplicationController.Page.CATEGORY);
    }

    @Required
    public void setEndpoint(Endpoint endpoint) {
        this.endpoint = endpoint;
    }

    @Required
    public void setUiTemplateSupport(UITemplateSupport uiTemplateSupport) {
        this.uiTemplateSupport = uiTemplateSupport;
    }

    @Required
    public void setApplicationController(ApplicationController applicationController) {
        this.applicationController = applicationController;
    }
}
