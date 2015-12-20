package com.adniewiagmail.findme.Persistence;

import android.util.Log;

import com.adniewiagmail.findme.BackgroundThreadsManager;

/**
 * Created by Adaś on 2015-11-15.
 */
public class DataManager {

    private static PendingInvitesProvider pendingInvitesProvider;
    private static MyFriendsProvider myFriendsProvider;

    public static PendingInvitesProvider pendingInvites(){
        if (pendingInvitesProvider == null) {
            pendingInvitesProvider = new PendingInvitesProvider();
        }
        return pendingInvitesProvider;
    }

    public static MyFriendsProvider myFriends() {
        if (myFriendsProvider == null) {
            myFriendsProvider = new MyFriendsProvider();
            BackgroundThreadsManager.startThread(myFriendsProvider);
        }
        return myFriendsProvider;
    }

    public static void clearFriends() {
        Log.d("DATA_MANAGER", "Clearing myFriends");
        if (myFriendsProvider != null) {
            myFriendsProvider.clear();
        }
    }

}
