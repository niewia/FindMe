package com.adniewiagmail.findme.Activities;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.parse.Parse;
import com.parse.ParseInstallation;

/**
 * Created by Adaś on 2015-11-12.
 */
public class FindMe extends Application {

    private static final String APPLICATION_ID = "x4VaGWxN8POWuOoanssAvds4DS4WT57sK9w9VpZN";
    private static final String CLIENT_KEY = "4osEHFfohwwXV9EPM1lLAisckERc5Vbrk7JDfk3y";
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();

        Parse.initialize(this, APPLICATION_ID, CLIENT_KEY);
        ParseInstallation.getCurrentInstallation().saveInBackground();
        FindMe.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return FindMe.context;
    }

    private void loadLoginView() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
