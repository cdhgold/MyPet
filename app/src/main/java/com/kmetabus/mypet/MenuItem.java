package com.kmetabus.mypet;

import android.graphics.drawable.Drawable;

public class MenuItem {
    private String title;
    private String description;
    private int imageResourceId;
    private String gbn; // 구분값

    public MenuItem(String title, String description,int imageResourceId, String gbn ) {
        this.title = title;
        this.description = description;
        this.imageResourceId = imageResourceId;
        this.gbn = gbn;

    }

    public String getTitle() {
        return title;
    }
    public String getDescription() {
        return description;
    }
    public int getImageResourceId() {
        return imageResourceId;
    }
    public String getGbn() {
        return gbn;
    }

}
