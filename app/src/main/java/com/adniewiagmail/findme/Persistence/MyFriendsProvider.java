package com.adniewiagmail.findme.Persistence;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.adniewiagmail.findme.BackgroundThreadsManager;
import com.adniewiagmail.findme.Persistence.DataObjects.Friend;
import com.adniewiagmail.findme.TerminatableThread;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Adaś on 2015-11-12.
 */
public class MyFriendsProvider extends TerminatableThread{

    private static List<Friend> friends = new ArrayList<Friend>();
    private Object pauseLock;
    private volatile boolean running;
    private volatile boolean paused;
    private boolean loading = false;

    public MyFriendsProvider() {
        this.pauseLock = new Object();
        this.running = true;
        this.paused = false;
    }

    public void terminate() {
        Log.d("BACKGROUND_THREAD", "Stoping MyFriendProvider");
        running = false;
        try {
            join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        while (running) {
            try {
                if (!loading) {
                    loadFriends();
                }
                Thread.sleep((long) BackgroundThreadsManager.defaultThreadUpdateInterval);
            } catch (InterruptedException e) {
                running = false;
            }
            synchronized (pauseLock) {
                while (paused) {
                    try {
                        pauseLock.wait();
                    } catch (InterruptedException e) {
                    }
                }
            }
        }
    }

    public void loadFriends() {
        loading = true;
        ParseQuery<ParseObject> myFriends = ParseQuery.getQuery("Friends");
        myFriends.whereEqualTo("user", ParseUser.getCurrentUser());
        myFriends.include("friends");
        myFriends.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject friendsRow, ParseException e) {
                if (e == null) {
                    friends.clear();
                    List<ParseUser> friendsObjects = friendsRow.getList("friends");
                    if (friendsObjects != null) {
                        for (ParseUser friendObject : friendsObjects) {
                            friends.add(new Friend(friendObject));
                        }
                    }
                } else {
                    Log.d(getClass().getSimpleName(), "Error loading friends: " + e.getMessage());
                }
                loading = false;
            }
        });
    }

    public void popualateList(Context context, final ArrayAdapter<Friend> adapter) {
        final ProgressDialog progress = new ProgressDialog(context);
        progress.show();
        ParseQuery<ParseObject> myFriends = ParseQuery.getQuery("Friends");
        myFriends.whereEqualTo("user", ParseUser.getCurrentUser());
        myFriends.include("friends");
        myFriends.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject friendRow, ParseException e) {
                if (e == null) {
                    friends.clear();
                    List<ParseUser> friendsObjects = friendRow.getList("friends");
                    if (friendsObjects != null) {
                        for (ParseUser friendObject : friendsObjects) {
                            friends.add(new Friend(friendObject));
                        }
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    Log.d(getClass().getSimpleName(), "Error: " + e.getMessage());
                }
                progress.dismiss();
            }
        });
    }

    public List<Friend> getFriends() {
        return friends;
    }

    public Friend findFriend(ParseUser user) {
        for (Friend friend : friends) {
            String friendId = friend.getId();
            String userId  = user.getObjectId();
            if (userId.equals(friendId)) {
                return friend;
            }
        }
        return null;
    }


    public void onPause() {
        Log.d("ACTIVITY_LIFECYCLE", "Pausing MyFriendsProvider");
        synchronized (pauseLock) {
            paused = true;
        }
    }

    public void onResume() {
        Log.d("ACTIVITY_LIFECYCLE", "Resuming MyFriendsProvider");
        synchronized (pauseLock) {
            paused = false;
            pauseLock.notifyAll();
        }
    }
}
