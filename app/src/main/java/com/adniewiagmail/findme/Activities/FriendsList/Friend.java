package com.adniewiagmail.findme.Activities.FriendsList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.util.DisplayMetrics;
import android.util.Log;

import com.adniewiagmail.findme.Activities.FindMe;
import com.adniewiagmail.findme.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
    private FriendMarker marker;
    private Bitmap profilePhoto;
    private ParseUser user;
    private Date updatedAt;
    private Address address;

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
        if (status == null) {
            status = FindMe.getAppContext().getString(R.string.statusNothingToSay);
        }
        this.status = status;
    }

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(ParseGeoPoint location) {
        this.location = new LatLng(location.getLatitude(), location.getLongitude());
        Geocoder gcd = new Geocoder(FindMe.getAppContext(), Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = gcd.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addresses.size() > 0) {
            setAddress(addresses.get(0));
        }
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Address getAddress() {
        return this.address;
    }

    public BitmapDescriptor getIcon() {
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
            marker = new FriendMarker(map, this);
        } else {
            marker.updateMarker(this);
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
            setUpdatedAt(updatedAt);
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
