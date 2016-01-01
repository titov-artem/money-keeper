package com.github.money.keeper.app;

import com.github.money.keeper.contoller.CategoryController;
import com.github.money.keeper.storage.memory.InMemoryFileBackedCategoryRepo;
import com.github.money.keeper.ui.Endpoint;
import com.github.money.keeper.ui.WebUIHolder;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class CategoryEditor extends Application {

    private static final String categoryEditorFXMLFile = "ui/html/category-editor.html";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        CategoryController controller = buildContext();
        Endpoint endpoint = new Endpoint();
        endpoint.register(controller);

        stage.setTitle("Category editor");
        WebUIHolder uiHolder = new WebUIHolder(categoryEditorFXMLFile, 750, 500);
        uiHolder.setMember("endpoint", endpoint);
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

        CategoryController categoryController = new CategoryController();
        categoryController.setCategoryRepo(categoryRepo);
        return categoryController;
    }

}
