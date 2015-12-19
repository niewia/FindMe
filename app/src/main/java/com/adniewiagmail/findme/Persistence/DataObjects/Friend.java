package com.adniewiagmail.findme.Persistence.DataObjects;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.util.Log;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

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
    private ParseGeoPoint location;
    private ParseUser user;

    public Friend(ParseObject friendObject) {
        setId(friendObject.getObjectId());
        setFirstName(friendObject.getString("firstName"));
        setLastName(friendObject.getString("lastName"));
        setEmail(friendObject.getString("email"));
        setLocation(friendObject.getParseGeoPoint("location"));
        setUsername(friendObject.getString("username"));
        setStatus(friendObject.getString("status"));
        setUser((ParseUser) friendObject);
    }

    public Friend(String friendId) {
        setId(friendId);
        getFriend();
    }

    private void getFriend() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("_User");
        query.whereEqualTo("objectId", getId());
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject friendObject, ParseException e) {
                if (e == null) {
                    setFirstName(friendObject.getString("firstName"));
                    setLastName(friendObject.getString("lastName"));
                    setEmail(friendObject.getString("email"));
                    setLocation(friendObject.getParseGeoPoint("location"));
                    setUsername(friendObject.getString("username"));
                    setStatus(friendObject.getString("status"));
                } else {
                    Log.d(getClass().getSimpleName(), "Error: " + e.getMessage());
                }
            }
        });
    }

    public ParseUser getUser() {
        return user;
    }

    public void setUser(ParseUser user) {
        this.user = user;
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
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ParseGeoPoint getLocation() {
        return location;
    }

    public void setLocation(ParseGeoPoint location) {
        this.location = location;
    }

    public void getLocation(ParseGeoPoint location) {
        this.location = location;
    }

    public Bitmap getProfilePhoto(Context context) {
        if (context == null) {
            return null;
        }
        DisplayMetrics metrics = context.getApplicationContext().getResources().getDisplayMetrics();
        byte[] profilePhotosData = getUser().getBytes("profile_photo");
        if (profilePhotosData == null ) {
            return null;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScreenDensity = metrics.densityDpi;
        options.inTargetDensity = metrics.densityDpi;
        options.inDensity = DisplayMetrics.DENSITY_DEFAULT;
        Bitmap profilePhoto = BitmapFactory.decodeByteArray(profilePhotosData, 0, profilePhotosData.length, options);
        return profilePhoto;
    }

    @Override
    public String toString() {
        return getFirstName() + " " + getLastName();
    }
}
