package com.adniewiagmail.findme.Activities.MainActivity;

/**
 * Created by Adaś on 2015-11-12.
 */

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.adniewiagmail.findme.Persistence.DataManager;
import com.adniewiagmail.findme.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

/**
 * A fragment that launches other parts of the demo application.
 */
public class MapFragment extends Fragment implements GoogleMap.OnMarkerClickListener {
    private MapView mMapView;
    private GoogleMap googleMap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // inflat and return the layout
        View v = inflater.inflate(R.layout.fragment_location_info, container,
                false);
        mMapView = (MapView) v.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();// needed to get the map to display immediately
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        googleMap = mMapView.getMap();
//        googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        googleMap.setOnMarkerClickListener(this);
        googleMap.setMyLocationEnabled(true);
        return v;
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
        DataManager.myFriends().onResume(googleMap);
    }

    @Override
    public void onPause() {
        DataManager.myFriends().onPause();
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    public void moveCamera(Location location) {
        if (location == null) {
            Log.d("LOCATION", "Unable to moveCamera: location is null");
            return;
        }
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        Log.d("moceCamera", "Moving camera to " + latitude + ", " + longitude);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(latitude, longitude)).zoom(12).build();
        googleMap.moveCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));

    }

    public void moveCamera(CameraPosition position) {
        googleMap.moveCamera(CameraUpdateFactory
                .newCameraPosition(position));
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        String snippet = marker.getSnippet();
        Log.d("MAP_FRAGMENT", marker.getTitle() + " marker was clicked, snippet: " + snippet);
        return false;
    }

    public GoogleMap getGoogleMap() {
        return this.googleMap;
    }
}
