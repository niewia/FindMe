package com.adniewiagmail.findme.Notifications;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.parse.ParseAnalytics;
import com.parse.ParsePushBroadcastReceiver;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Adaś on 2015-11-22.
 */
public class NotificationReceiver extends ParsePushBroadcastReceiver {

    @Override
    protected void onPushOpen(Context context, Intent intent) {
        ParseAnalytics.trackAppOpenedInBackground(intent);
        String uriString = null;

        try {
            JSONObject cls = new JSONObject(intent.getStringExtra("com.parse.Data"));
            uriString = cls.optString("uri", (String)null);
        } catch (JSONException var6) {
            Log.e("ParsePushReceiver", "Unexpected JSONException when receiving push data: ", var6);
        }

        Class cls1 = this.getActivity(context, intent);
        Intent activityIntent;
        if(uriString != null) {
            activityIntent = new Intent("android.intent.action.VIEW", Uri.parse(uriString));
        } else {
            activityIntent = new Intent(context, cls1);
        }
        activityIntent.putExtras(intent.getExtras());
        activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(activityIntent);
    }

    @Override
    protected Class<? extends Activity> getActivity(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        String jsonData = extras.getString("com.parse.Data");
        String className;
        try {
            JSONObject json = new JSONObject(jsonData);
            className = json.getString("activity");
        } catch (JSONException e) {
            return null;
        }
        Class cls = null;
        try {
            cls = Class.forName(className);
        } catch (ClassNotFoundException var8) {
            ;
        }
        return cls;
    }
}
