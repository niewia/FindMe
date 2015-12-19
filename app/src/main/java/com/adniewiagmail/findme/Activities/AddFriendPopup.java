package com.adniewiagmail.findme.Activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.adniewiagmail.findme.Persistence.DataManager;
import com.adniewiagmail.findme.Persistence.DataObjects.Friend;
import com.adniewiagmail.findme.R;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Adaś on 2015-11-15.
 */
public class AddFriendPopup {
    private final Activity activity;
    private TextView textViewFriendName;
    private Button buttonConfirm;
    private Button buttonCancel;
    private PopupWindow statusPopup;
    private Friend friendToAdd;
    private ProgressDialog progress;

    public AddFriendPopup(PendingInvitesList pendingInvitesList, Friend friend) {
        this(pendingInvitesList, friend, "");
    }

    public AddFriendPopup(Activity activity, Friend friendToAdd, String message) {
        this.activity = activity;
        LayoutInflater inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.popup_add_friend,
                (ViewGroup) activity.findViewById(R.id.popup_element_add_friend));
        statusPopup = new PopupWindow(layout);
        statusPopup.setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
        statusPopup.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        this.friendToAdd = friendToAdd;
        textViewFriendName = (TextView) layout.findViewById(R.id.addFriendPopupFriendName);
        textViewFriendName.setText(friendToAdd.toString() + message);
        buttonConfirm = (Button) layout.findViewById(R.id.buttonAddFriedConfirm);
        buttonConfirm.setOnClickListener(addFriend);
        buttonCancel = (Button) layout.findViewById(R.id.buttonAddFriendCanel);
        buttonCancel.setOnClickListener(cancel);
        statusPopup.showAtLocation(layout, Gravity.CENTER, 0, 0);
    }

    private View.OnClickListener cancel = new View.OnClickListener() {
        public void onClick(View v) {
            statusPopup.dismiss();
        }
    };
    private View.OnClickListener addFriend = new View.OnClickListener() {
        public void onClick(View v) {
            statusPopup.dismiss();
            progress = new ProgressDialog(activity);
            progress.show();
            Map<String, String> params = new HashMap<String, String>();
            params.put("userId", ParseUser.getCurrentUser().getObjectId());
            params.put("newFriendId", friendToAdd.getUser().getObjectId());
            ParseCloud.callFunctionInBackground("addFriend", params, new FunctionCallback<Object>() {
                @Override
                public void done(Object o, ParseException e) {
                    progress.dismiss();
                    if (e == null) {
                        notifyFriendAddedSuccessfully();
                    } else {
                        Log.d("ADD_FRIEND", e.getMessage());
                    }
                }
            });
        }
    };

    private void notifyFriendAddedSuccessfully() {
        Toast toast = Toast.makeText(activity, friendToAdd.toString() + activity.getString(R
                        .string
                        .friendAddedSuccessfullyToast),
                Toast.LENGTH_SHORT);
        toast.show();
        if (activity instanceof PendingInvitesList) {
            PendingInvitesList pendingInvitesList = (PendingInvitesList) activity;
            DataManager.pendingInvites().populateList(pendingInvitesList);
        }
    }
}
