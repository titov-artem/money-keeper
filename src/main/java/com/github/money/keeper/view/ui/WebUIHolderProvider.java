package com.github.money.keeper.view.ui;

import com.google.common.base.Preconditions;

/**
 * @author Artem Titov
 */
public enum WebUIHolderProvider {
    INSTANCE;

    private WebUIHolder webUIHolder;

    public WebUIHolder getWebUIHolder() {
        Preconditions.checkState(webUIHolder != null,
                "Application context not fully initialized! Please inject JavaFX WebUIHolder into WebUIHolderProvider");
        return webUIHolder;
    }

    public void setWebUIHolder(WebUIHolder webUIHolder) {
        this.webUIHolder = webUIHolder;
    }
}
