package com.adniewiagmail.findme.Persistence.DataObjects;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.util.Log;

import com.adniewiagmail.findme.Activities.FindMe;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.Date;

/**
 * Created by Adaś on 2015-11-12.
 */
public class Friend {
    private String id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String status;
    private LatLng location;
    private Marker marker;
    private Bitmap profilePhoto;
    private ParseUser user;
    private Date updatedAt;

    public Friend(ParseObject friendObject) {
        setFriendData(friendObject);
    }

    public Friend(ParseUser friendUser, GoogleMap googleMap) {
        this(friendUser);
        updateMarker(googleMap);
    }

    private void setFriendData(ParseObject friendObject) {
        setId(friendObject.getObjectId());
        setFirstName(friendObject.getString("firstName"));
        setLastName(friendObject.getString("lastName"));
        setEmail(friendObject.getString("email"));
        setLocation(friendObject.getParseGeoPoint("location"));
        setUsername(friendObject.getString("username"));
        setStatus(friendObject.getString("status"));
        setProfilePhoto(friendObject.getBytes("profile_photo"));
        setUser((ParseUser) friendObject);
        setUpdatedAt(friendObject.getUpdatedAt());
    }

    public Friend(String friendId) {
        getFriendData(friendId);
    }

    private void getFriendData(String friendId) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("_User");
        query.whereEqualTo("objectId", getId());
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject friendObject, ParseException e) {
                if (e == null) {
                    setFriendData(friendObject);
                } else {
                    Log.d(getClass().getSimpleName(), "Error: " + e.getMessage());
                }
            }
        });
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String nickname) {
        this.username = nickname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getStatus() {
        return "\"" + status + "\"";
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(ParseGeoPoint location) {
        this.location = new LatLng(location.getLatitude(), location.getLongitude());
    }

    private BitmapDescriptor getIcon() {
        Bitmap profilePhoto = getProfilePhoto();
        BitmapDescriptor icon;
        if (profilePhoto == null) {
            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE);
        } else {
            icon = BitmapDescriptorFactory.fromBitmap(profilePhoto);
        }
        return icon;
    }

    public void updateMarker(GoogleMap map) {
        if (marker == null) {
            Log.d("FRIEND", "Creating new marker");
            MarkerOptions options = new MarkerOptions()
                    .position(getLocation())
                    .title(getUsername())
                    .snippet(getStatus())
                    .icon(getIcon());
            this.marker = map.addMarker(options);
        } else {
            if (!marker.getPosition().equals(getLocation())) {
                Log.d("FRIEND", "Updating marker location");
                marker.setPosition(getLocation());
            }
            if (!marker.getTitle().equals(getUsername())) {
                Log.d("FRIEND", "Updating marker title");
                marker.setTitle(getUsername());
            }
            if (!marker.getSnippet().equals(getStatus())) {
                Log.d("FRIEND", "Updating marker snippet");
                marker.setSnippet(getStatus());
            }
            marker.setIcon(getIcon());
        }
    }

    public void removeMarker() {
        marker = null;
    }

    public Bitmap getProfilePhoto() {
        return profilePhoto;
    }

    @Override
    public String toString() {
        return getFirstName() + " " + getLastName();
    }

    public void updateData(ParseUser friendObject, GoogleMap map) {
        Date updatedAt = friendObject.getUpdatedAt();
        if (isDataOld(updatedAt)) {
            setLocation(friendObject.getParseGeoPoint("location"));
            setStatus(friendObject.getString("status"));
            setProfilePhoto(friendObject.getBytes("profile_photo"));
            updateMarker(map);
        } else {
            Log.d("FRIEND", "FRIEND DATA IS UP TO DATE");
        }
    }

    private boolean isDataOld(Date updatedAt) {
        return getUpdatedAt().before(updatedAt);
    }

    public void setProfilePhoto(byte[] profilePhotoData) {
        Context context = FindMe.getAppContext();
        if (context == null) {
            profilePhoto = null;
            return;
        }
        DisplayMetrics metrics = context.getApplicationContext().getResources().getDisplayMetrics();
        if (profilePhotoData == null) {
            profilePhoto = null;
            return;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScreenDensity = metrics.densityDpi;
        options.inTargetDensity = metrics.densityDpi;
        options.inDensity = DisplayMetrics.DENSITY_DEFAULT;
        this.profilePhoto = BitmapFactory.decodeByteArray(profilePhotoData, 0, profilePhotoData.length, options);
    }

    public ParseUser getUser() {
        return user;
    }

    public void setUser(ParseUser user) {
        this.user = user;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }
}
