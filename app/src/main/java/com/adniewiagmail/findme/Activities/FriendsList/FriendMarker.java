package com.adniewiagmail.findme.Activities.FriendsList;

import android.os.Handler;
import android.os.Looper;

import com.adniewiagmail.findme.Activities.FriendsList.Friend;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by Adaś on 2016-01-06.
 */
public class FriendMarker {

    Handler mainHandler = new Handler(Looper.getMainLooper());
    private Marker marker;

    FriendMarker(GoogleMap map, Friend friend) {
        MarkerOptions options = new MarkerOptions()
                .position(friend.getLocation())
                .title(friend.getUsername())
                .snippet(friend.getStatus())
                .icon(friend.getIcon());
        this.marker = map.addMarker(options);
    }

    public void updateMarker(Friend friend) {
        boolean shouldShowInfo = marker.isInfoWindowShown();
        marker.setIcon(friend.getIcon());
        if (!marker.getPosition().equals(friend.getLocation())) {
            marker.setPosition(friend.getLocation());
        }
        if (!marker.getTitle().equals(friend.getUsername())) {
            marker.setTitle(friend.getUsername());
        }
        if (!marker.getSnippet().equals(friend.getStatus())) {
            marker.setSnippet(friend.getStatus());
            if (!shouldShowInfo) {
                showNewStatus();
            }
        }
        if (shouldShowInfo) {
            marker.showInfoWindow();
        }
    }

    private void showNewStatus() {
        Thread show = new Thread() {
            public void run() {
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        marker.showInfoWindow();
                    }
                });
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException v) {
                }
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        marker.hideInfoWindow();
                    }
                });
            };
        };
        show.start();
    }

}
