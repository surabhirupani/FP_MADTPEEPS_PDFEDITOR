package com.example.pdfeditormadtpeeps.Utility;

import android.app.Application;

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        TypefaceUtil.setDefaultFont(this, "DEFAULT", "fonts/PoppinsLight.ttf");
        TypefaceUtil.setDefaultFont(this, "MONOSPACE", "fonts/PoppinsLight.ttf");
        TypefaceUtil.setDefaultFont(this, "SANS_SERIF", "fonts/PoppinsLight.ttf");
        TypefaceUtil.setDefaultFont(this, "SERIF", "fonts/PoppinsLight.ttf");
    }
}
