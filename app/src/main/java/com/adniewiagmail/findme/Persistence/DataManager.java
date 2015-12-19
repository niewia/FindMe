package com.adniewiagmail.findme.Persistence;

import com.adniewiagmail.findme.Activities.MainActivity.MapUpdater;
import com.adniewiagmail.findme.BackgroundThreadsManager;

/**
 * Created by Adaś on 2015-11-15.
 */
public class DataManager {

    private static PendingInvitesProvider pendingInvitesProvider;
    private static MyFriendsProvider myFriendsProvider;
    private static MapUpdater mapUpdater;

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

    public static MapUpdater mapUpdater() {
        if (mapUpdater == null) {
            mapUpdater = new MapUpdater();
            BackgroundThreadsManager.startThread(mapUpdater);
        }
        return mapUpdater;
    }
}
