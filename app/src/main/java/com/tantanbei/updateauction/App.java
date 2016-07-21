package com.tantanbei.updateauction;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

public class App extends Application {

    static public Handler Uihandler = new Handler(Looper.getMainLooper());

    @Override
    public void onCreate() {
        super.onCreate();

        initIconify();

    }

    private void initIconify(){
//        Iconify.with(new TypiconsModule());
//        Iconify.with(new FontAwesomeModule());
//        Iconify.with(new EntypoModule());
//        Iconify.with(new MaterialModule());
//        Iconify.with(new IoniconsModule());
    }
}