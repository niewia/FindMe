package com.adniewiagmail.findme.Activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.adniewiagmail.findme.R;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

/**
 * Created by Adaś on 2015-11-12.
 */
public class UpdateStatusPopup {

    private Button buttonUpdate;
    private Button buttonCancel;
    private EditText textStatus;
    private PopupWindow statusPopup;
    private Activity activity;

    public UpdateStatusPopup(Activity activity) {
        this.activity = activity;
        LayoutInflater inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.popup_update_status,
                (ViewGroup) activity.findViewById(R.id.popup_element));
        statusPopup = new PopupWindow(layout);
        statusPopup.setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
        statusPopup.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        statusPopup.showAtLocation(layout, Gravity.TOP, 0, 100);
        statusPopup.setFocusable(true);
        statusPopup.update();
        buttonUpdate = (Button) layout.findViewById(R.id.buttonUpdate);
        buttonUpdate.setOnClickListener(updateStatus);

        buttonCancel = (Button) layout.findViewById(R.id.buttonCanel);
        buttonCancel.setOnClickListener(updateCancel);

        textStatus = (EditText) layout.findViewById(R.id.textStatus);
        String status = ParseUser.getCurrentUser().getString("status");
        if (status != null && !status.isEmpty()) {
            textStatus.setText(status);
        }
        textStatus.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    saveStatus();
                    statusPopup.dismiss();
                    handled = true;
                }
                return handled;
            }
        });
    }

    private View.OnClickListener updateCancel = new View.OnClickListener() {
        public void onClick(View v) {
            statusPopup.dismiss();
        }
    };

    private View.OnClickListener updateStatus = new View.OnClickListener() {
        public void onClick(View v) {
            saveStatus();
            statusPopup.dismiss();
        }
    };

    private void saveStatus() {
        final ProgressDialog progress = new ProgressDialog(activity);
        progress.setTitle(activity.getString(R.string.statusUpdateInProgress));
        progress.show();
        ParseUser currentUser = ParseUser.getCurrentUser();
        currentUser.put("status", textStatus.getText().toString());
        currentUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                progress.dismiss();
                Toast toast = Toast.makeText(activity, R.string.statusUpdateSuccess,
                        Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }
}
