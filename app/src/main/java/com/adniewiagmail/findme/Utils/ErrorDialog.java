package com.adniewiagmail.findme.Utils;

import android.app.Activity;
import android.support.v7.app.AlertDialog;

import com.adniewiagmail.findme.R;

/**
 * Created by Adaś on 2015-11-13.
 */
public class ErrorDialog {
    public ErrorDialog(Activity activity, String Message, String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(Message)
                .setTitle(R.string.signup_error_title)
                .setPositiveButton(android.R.string.ok, null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
