package com.browser.interfaces;

public interface BrowserListener {
    void onToolbarVisibilityUpdate(int isVisible);

    void onProgressBarUpdate(int isVisible);
}
