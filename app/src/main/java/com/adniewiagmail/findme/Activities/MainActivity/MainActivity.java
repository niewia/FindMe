package com.adniewiagmail.findme.Activities.MainActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Window;

import com.adniewiagmail.findme.Activities.LoginActivity;
import com.adniewiagmail.findme.BackgroundThreadsManager;
import com.adniewiagmail.findme.R;
import com.adniewiagmail.findme.Utils.PermissionCodes;
import com.google.android.gms.maps.model.CameraPosition;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class MainActivity extends FragmentActivity implements LocationListener {
    private static final long GPS_UPDATE_TIME_INTERVAL = BackgroundThreadsManager.defaultThreadUpdateInterval;
    private LocationManager locationManager;
    private MapFragment mapFragment;
    private MenuFragment menuFragment;
    private boolean shouldMoveToMyLocation = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser == null) {
            loadLoginView();
        } else {
            Log.d("MAIN_ACTIVITY", "onCreate method map fragment");
            mapFragment = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
            menuFragment = (MenuFragment) getSupportFragmentManager().findFragmentById(R.id.menuFragment);
            menuFragment.setMapFragment(mapFragment);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        requestLocationUpdate();
        moveCameraIfPossible();
    }

    private void moveCameraIfPossible() {
        CameraPosition cameraPosition = getIntent().getParcelableExtra("cameraPosition");
        if (cameraPosition != null) {
            mapFragment.moveCamera(cameraPosition);
        }
    }

    private void loadLoginView() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        PermissionCodes.MY_PERMISSIONS_REQUEST_ACCESS_LOCATION);
            }
        }
        locationManager.removeUpdates(this);
    }

    private void requestLocationUpdate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission
                .ACCESS_FINE_LOCATION) != PackageManager
                .PERMISSION_GRANTED &&
                checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager
                        .PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    PermissionCodes.MY_PERMISSIONS_REQUEST_ACCESS_LOCATION);
        } else {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, GPS_UPDATE_TIME_INTERVAL, 0, this);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, GPS_UPDATE_TIME_INTERVAL, 0, this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PermissionCodes.MY_PERMISSIONS_REQUEST_ACCESS_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    requestLocationUpdate();
                } else {
                    finish();
                    System.exit(0);
                }
                return;
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if (shouldMoveToMyLocation && getIntent().getParcelableExtra("cameraPosition") == null) {
            shouldMoveToMyLocation = false;
            mapFragment.moveCamera(location);
        }
        updateGeoPoint(location);
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("LOCATION", "status");
    }

    private void updateGeoPoint(Location location) {
        ParseUser currentUser = ParseUser.getCurrentUser();
        ParseGeoPoint geoPoint = new ParseGeoPoint();
        geoPoint.setLatitude(location.getLatitude());
        geoPoint.setLongitude(location.getLongitude());
        currentUser.put("location", geoPoint);
        currentUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("LOCATION", "User location saved successfully");
                } else {
                    Log.d("LOCATION", "Unable to save user location: " + e.getMessage());
                }
            }
        });
    }
}

