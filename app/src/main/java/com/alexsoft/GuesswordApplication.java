package com.alexsoft;

import android.app.Application;
import android.content.Context;

public class GuesswordApplication extends Application {

    private static Application guesswordApplication;

    public static Application getApplication() {
        return guesswordApplication;
    }

    public static Context getAppContext() {
        return getApplication().getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        guesswordApplication = this;
    }
}