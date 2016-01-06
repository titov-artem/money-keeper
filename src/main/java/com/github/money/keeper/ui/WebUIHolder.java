package com.github.money.keeper.ui;

import com.github.money.keeper.template.UITemplateSupport;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.layout.Region;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebEvent;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.Map;

public final class WebUIHolder extends Region {
    private static final Logger log = LoggerFactory.getLogger(WebUIHolder.class);

    private final WebView webView = new WebView();
    private final WebEngine webEngine = webView.getEngine();

    private final int preferredWidth;
    private final int preferredHeight;

    private final Map<String, Object> permanentMembers = Maps.newHashMap();

    public WebUIHolder(String htmlUiFileName,
                       int preferredWidth,
                       int preferredHeight,
                       Endpoint endpoint,
                       UITemplateSupport templateSupport) {
        this(preferredWidth, preferredHeight, endpoint, templateSupport);
        switchView(htmlUiFileName);
    }

    public WebUIHolder(int preferredWidth,
                       int preferredHeight,
                       Endpoint endpoint,
                       UITemplateSupport templateSupport) {
        this.preferredWidth = preferredWidth;
        this.preferredHeight = preferredHeight;

        getChildren().add(webView);
        webEngine.getLoadWorker().exceptionProperty().addListener((observable, oldValue, newValue) -> {
            log.error("Exception in WebEngine acquired: " + observable, newValue);
        });

        webEngine.getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
            log.debug("WebEngine: old state: " + observable + ", new state: " + oldValue + ", arg0: " + newValue);
        });
        webEngine.setOnAlert(new EventHandler<WebEvent<String>>() {
            @Override
            public void handle(WebEvent<String> event) {
                if (event.getData().equals("command:inject")) {
                    // it looks like that on alert listener invokes synchronously to alerts in js, so while we are int
                    // the listener no JS will be executed, so we can set members and all will work correctly
                    permanentMembers.forEach(WebUIHolder.this::setMember);
                }
            }
        });
        setPermanentMember("endpoint", endpoint);
        setPermanentMember("templates", templateSupport);
    }

    public void switchView(String htmlViewFileName) {
        URL resource = getClass().getClassLoader().getResource(htmlViewFileName);
//        URL resource = null;
//        try {
//            resource = new URL("http://getbootstrap.com/components/#input-groups");
//        } catch (MalformedURLException e) {
//            throw new RuntimeException(e);
//        }
        Preconditions.checkNotNull(resource, "Failed to load UI from file " + htmlViewFileName);
        Platform.runLater(() -> webEngine.load(resource.toExternalForm()));
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

    public void setPermanentMember(String name, Object object) {
        setMember(name, object);
        permanentMembers.put(name, object);
    }
}
