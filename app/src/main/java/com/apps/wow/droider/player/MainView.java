package com.apps.wow.droider.player;


import android.widget.ImageButton;

/**
 * Created by Jackson on 06/01/2017.
 */

public interface MainView {

    boolean isControlActivated();

    void setIsControlActivated(boolean isActivated);

    void setControlButtonImageResource(int resource);

    void setVisibilityToControlButton(int visibility);

    ImageButton getControlButton();

    void refreshNotification();

    void showToast(String text);

    String getPodcastTitle();
}
