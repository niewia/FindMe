package com.adniewiagmail.findme.Activities.FriendsList;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.adniewiagmail.findme.Activities.FindFriends;
import com.adniewiagmail.findme.Activities.MainActivity.MainActivity;
import com.adniewiagmail.findme.Persistence.DataManager;
import com.adniewiagmail.findme.R;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseUser;

/**
 * Created by Adaś on 2015-11-15.
 */
public class MyFriendsList extends ListActivity {

    private Handler mainHandler = new Handler(Looper.getMainLooper());
    private Button findFriends;
    private FriendsListAdapter adapter;
    private Thread friendsUpdater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_friends);
        findFriends = (Button)findViewById(R.id.emptyFriendsListButton);
        getListView().setEmptyView(findFriends);
        MyFriendsProvider myFriendsProvider = DataManager.myFriends();
        adapter = new FriendsListAdapter(MyFriendsList.this, myFriendsProvider.getFriends());
        setListAdapter(adapter);
        myFriendsProvider.setFriendsListAdapter(adapter);
        findFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyFriendsList.this, FindFriends.class);
                Parcelable cameraPosition = getIntent().getParcelableExtra("cameraPosition");
                intent.putExtra("cameraPosition", cameraPosition);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Friend friendSelected = (Friend)l.getAdapter().getItem(position);
        Intent intent = getIntentWithLocation(friendSelected.getUser());
        startActivity(intent);
    }

    @Override
    public void onPause() {
        super.onPause();
        DataManager.myFriends().setFriendsListDisplayed(false);
    }

    @Override
    public void onBackPressed() {
        Parcelable cameraPosition = getIntent().getParcelableExtra("cameraPosition");
        Intent intent = new Intent(MyFriendsList.this, MainActivity.class);
        intent.putExtra("cameraPosition", cameraPosition);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private Intent getIntentWithLocation(ParseUser user) {
        Intent intent = new Intent(MyFriendsList.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        double latitude = user.getParseGeoPoint("location").getLatitude();
        double longitude = user.getParseGeoPoint("location").getLongitude();
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(latitude, longitude)).zoom(12).build();
        intent.putExtra("cameraPosition", cameraPosition);
        return intent;
    }
}
