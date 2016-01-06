package com.adniewiagmail.findme.Activities.PendingInvites;

import android.app.ProgressDialog;
import android.util.Log;
import android.widget.Button;

import com.adniewiagmail.findme.Utils.FriendshipStatus;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Adaś on 2015-11-13.
 */
public class PendingInvitesProvider {
    private List<Invite> invites = new ArrayList<Invite>();

    public void loadInvites(final Button pendingInvitesButton) {
        ParseQuery<ParseObject> myFriends = ParseQuery.getQuery("FriendInvitation");
        myFriends.whereEqualTo("status", FriendshipStatus.PENDING);
        myFriends.whereEqualTo("userTo", ParseUser.getCurrentUser());
        myFriends.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> invitesObjects, ParseException e) {
                if (e == null) {
                    invites.clear();
                    for (ParseObject inviteObject : invitesObjects) {
                        invites.add(new Invite(inviteObject));
                    }
                    String text = pendingInvitesButton.getText().toString();
                    text = text + " (" + invites.size() + ")";
                    pendingInvitesButton.setText(text);
                } else {
                    Log.d(getClass().getSimpleName(), "Error: " + e.getMessage());
                }
            }
        });
    }

    public void populateList(final PendingInvitesList pendingInvitesList) {
        final ProgressDialog progress = new ProgressDialog(pendingInvitesList);
        progress.show();
        ParseQuery<ParseObject> myFriends = ParseQuery.getQuery("FriendInvitation");
        myFriends.whereEqualTo("status", FriendshipStatus.PENDING);
        myFriends.whereEqualTo("userTo", ParseUser.getCurrentUser());
        myFriends.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> invitesObjects, ParseException e) {
                if (e == null) {
                    invites.clear();
                    for (ParseObject inviteObject : invitesObjects) {
                        invites.add(new Invite(inviteObject));
                    }
                    pendingInvitesList.getAdapter().notifyDataSetChanged();
                } else {
                    Log.d(getClass().getSimpleName(), "Error: " + e.getMessage());
                }
                progress.dismiss();
            }
        });
    }

    public List<Invite> getInvites() {
        return invites;
    }
}
