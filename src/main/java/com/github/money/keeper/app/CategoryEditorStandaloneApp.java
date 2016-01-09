package com.github.money.keeper.app;

import com.github.money.keeper.contoller.CategoryController;
import com.github.money.keeper.storage.memory.InMemoryFileBackedCategoryRepo;
import com.github.money.keeper.template.TemplateSupport;
import com.github.money.keeper.template.UITemplateSupport;
import com.github.money.keeper.ui.Endpoint;
import com.github.money.keeper.ui.WebUIHolder;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class CategoryEditorStandaloneApp extends Application {

    private static final String categoryEditorFXMLFile = "ui/html/category/category-editor.html";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        CategoryController controller = buildContext();
        Endpoint endpoint = new Endpoint();
        endpoint.register(controller);

        TemplateSupport sourceTemplateSupport = new TemplateSupport();
        sourceTemplateSupport.init();
        UITemplateSupport templateSupport = new UITemplateSupport();
        templateSupport.setTemplateSupport(sourceTemplateSupport);

        stage.setTitle("Category editor");
        WebUIHolder uiHolder = new WebUIHolder(
                categoryEditorFXMLFile,
                800,
                500,
                endpoint,
                templateSupport
        );
        Scene scene = new Scene(uiHolder);
        stage.setScene(scene);
        stage.show();
    }

    private CategoryController buildContext() throws IOException {
        InMemoryFileBackedCategoryRepo categoryRepo = new InMemoryFileBackedCategoryRepo();
        String categoriesFileName = "categories.json";
        if (!new File(categoriesFileName).exists()) {
            new File(categoriesFileName).createNewFile();
        }
        categoryRepo.setStorageFileName(categoriesFileName);
        categoryRepo.init();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                categoryRepo.destroy();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));

        CategoryController categoryController = new CategoryController();
        categoryController.setCategoryRepo(categoryRepo);
        return categoryController;
    }

}