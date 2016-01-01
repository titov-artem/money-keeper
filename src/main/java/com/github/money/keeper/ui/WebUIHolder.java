package com.github.money.keeper.ui;

import com.google.common.base.Preconditions;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.layout.Region;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;

import java.net.URL;

public final class WebUIHolder extends Region {

    private final WebView webView = new WebView();
    private final WebEngine webEngine = webView.getEngine();

    private final int preferredWidth;
    private final int preferredHeight;

    public WebUIHolder(String htmlUiFile, int preferredWidth, int preferredHeight) {
        this.preferredWidth = preferredWidth;
        this.preferredHeight = preferredHeight;

        URL resource = getClass().getClassLoader().getResource(htmlUiFile);
        Preconditions.checkNotNull(resource, "Failed to load UI from file " + htmlUiFile);
        webEngine.load(resource.toExternalForm());
        getChildren().add(webView);
    }

    @Override
    protected void layoutChildren() {
        double w = getWidth();
        double h = getHeight();
        layoutInArea(webView, 0, 0, w, h, 0, HPos.CENTER, VPos.CENTER);
    }

    @Override
    protected double computePrefWidth(double height) {
        return preferredWidth;
    }

    @Override
    protected double computePrefHeight(double width) {
        return preferredHeight;
    }

    public void setMember(String name, Object object) {
        JSObject window = (JSObject) webEngine.executeScript("window");
        window.setMember(name, object);
    }
}
