package com.adniewiagmail.findme.Activities.FriendsList;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.adniewiagmail.findme.BackgroundThreadsManager;
import com.adniewiagmail.findme.TerminatableThread;
import com.google.android.gms.maps.GoogleMap;
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
public class MyFriendsProvider extends TerminatableThread {
    Handler mainHandler = new Handler(Looper.getMainLooper());
    private static List<Friend> friends = new ArrayList<Friend>();
    private Object pauseLock;
    private volatile boolean running;
    private volatile boolean paused;
    private boolean loading = false;
    private GoogleMap googleMap;
    private boolean isFriendsListDisplayed;
    private FriendsListAdapter friendsListAdapter;

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
            long time = System.currentTimeMillis();
            if (!loading) {
                loadFriends();
            }
            waitTimeLeft(time);
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
                    List<ParseUser> friendsObjects = friendsRow.getList("friends");
                    if (friendsObjects != null) {
                        updateFriends(friendsObjects);
                    }
                } else {
                    Log.d(getClass().getSimpleName(), "Error loading friends: " + e.getMessage());
                }
                loading = false;
            }
        });
    }

    private void updateFriends(List<ParseUser> friendsUsers) {
        for (ParseUser friendUser : friendsUsers) {
            Friend currFriend = findFriend(friendUser);
            if (currFriend == null) {
                friends.add(new Friend(friendUser, googleMap));
            } else {
                currFriend.updateData(friendUser, googleMap);
            }
            if (isFriendsListDisplayed) {
                friendsListAdapter.notifyDataSetChanged();
            }
        }
    }

    public List<Friend> getFriends() {
        return friends;
    }

    public Friend findFriend(ParseUser user) {
        for (Friend friend : friends) {
            String friendId = friend.getId();
            String userId = user.getObjectId();
            if (userId.equals(friendId)) {
                return friend;
            }
        }
        return null;
    }

    private boolean isInFriends(ParseUser user) {
        return findFriend(user) != null;
    }

    public void onPause() {
        Log.d("ACTIVITY_LIFECYCLE", "Pausing MyFriendsProvider");
        synchronized (pauseLock) {
            paused = true;
            removeOldMarkers();
        }
    }

    public void onResume(GoogleMap googleMap) {
        Log.d("ACTIVITY_LIFECYCLE", "Resuming MyFriendsProvider");
        synchronized (pauseLock) {
            this.googleMap = googleMap;
            updateMarkers();
            paused = false;
            pauseLock.notifyAll();
        }
    }

    public void removeOldMarkers() {
        for (Friend friend : friends) {
            friend.removeMarker();
        }
        googleMap.clear();
    }

    public void updateMarkers() {
        Log.d("MY_FRIENDS_PROVIDER", "Updating markers...");
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                for (Friend friend : friends) {
                    friend.updateMarker(googleMap);
                }
            }
        });
    }

    private void waitTimeLeft(long timeStarted) {
        long timeLeft = timeStarted - System.currentTimeMillis();
        timeLeft = (long) BackgroundThreadsManager.defaultThreadUpdateInterval - timeLeft;
        if (timeLeft < 1) {
            timeLeft = 1;
        }
        try {
            Log.d("SLEEP", "Sleeping for "+ timeLeft);
            Thread.sleep(timeLeft);
        } catch (InterruptedException e) {
            running = false;
        }
    }

    public void clear() {
        friends.clear();
    }

    public void setFriendsListAdapter(FriendsListAdapter friendsListAdapter) {
        this.friendsListAdapter = friendsListAdapter;
        setFriendsListDisplayed(true);
    }

    public void setFriendsListDisplayed(boolean isDisplayed) {
        this.isFriendsListDisplayed = isDisplayed;
    }
}
