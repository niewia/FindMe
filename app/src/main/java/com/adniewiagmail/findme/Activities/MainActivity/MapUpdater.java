package com.adniewiagmail.findme.Activities.MainActivity;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.adniewiagmail.findme.BackgroundThreadsManager;
import com.adniewiagmail.findme.Persistence.DataManager;
import com.adniewiagmail.findme.Persistence.DataObjects.Friend;
import com.adniewiagmail.findme.TerminatableThread;

import java.util.List;

/**
 * Created by Adaś on 2015-11-29.
 */
public class MapUpdater extends TerminatableThread{

    Handler mainHandler = new Handler(Looper.getMainLooper());
    List<Friend> friends;
    private MapFragment map;
    private Object pauseLock;
    private volatile boolean running;
    private volatile boolean paused;

    public MapUpdater() {
        this.pauseLock = new Object();
        this.running = true;
        this.paused = false;
    }

    public void terminate() {
        Log.d("BACKGROUND_THREAD", "Stopping MapUpdater");
        running = false;
        try {
            join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        while (running) {
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    updateMap();
                }
            });
            try {
                if (!paused) {
                    Thread.sleep(BackgroundThreadsManager.mapUpdateInterval);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
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

    public void updateMap() {
        List<Friend> friends = DataManager.myFriends().getFriends();
        if (friends.isEmpty()) {
            Log.d("MAP_UPDATER", "There is no friends to update");
            return;
        }
        map.removeAllMarkers();
        for (Friend friend : friends) {
            map.addMarker(friend);
        }
    }

    public void onPause() {
        Log.d("ACTIVITY_LIFECYCLE", "Pausing MapUpdater");
        synchronized (pauseLock) {
            paused = true;
        }
    }

    public void onResume(MapFragment map) {
        Log.d("ACTIVITY_LIFECYCLE", "Resuming MapUpdater");
        synchronized (pauseLock) {
            this.map = map;
            paused = false;
            pauseLock.notifyAll();
        }
    }
}
