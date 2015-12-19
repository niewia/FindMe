package com.adniewiagmail.findme.Persistence;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import com.adniewiagmail.findme.Activities.PendingInvitesList;
import com.adniewiagmail.findme.Persistence.DataObjects.Invite;
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

    public void loadInvites(Context context) {
        final ProgressDialog progress = new ProgressDialog(context);
        progress.show();
        ParseQuery<ParseObject> myFriends = ParseQuery.getQuery("FriendInvitation");
        myFriends.whereEqualTo("status", FriendshipStatus.PENDING);
        myFriends.whereEqualTo("userTo", ParseUser.getCurrentUser());
        myFriends.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> invitesObjects, ParseException e) {
                progress.dismiss();
                if (e == null) {
                    for (ParseObject inviteObject : invitesObjects) {
                        invites.add(new Invite(inviteObject));
                    }
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

    public Invite findInvite(ParseUser user) {
        for (Invite invite : invites) {
            String userFromId = invite.getUserFrom().getObjectId();
            String inviteId = user.getObjectId();
            if (userFromId.equals(inviteId)) {
                return invite;
            }
        }
        return null;
    }
}
