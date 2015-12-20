package com.adniewiagmail.findme.Activities;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.adniewiagmail.findme.Activities.MainActivity.MainActivity;
import com.adniewiagmail.findme.Persistence.DataManager;
import com.adniewiagmail.findme.Persistence.DataObjects.Friend;
import com.adniewiagmail.findme.Persistence.MyFriendsProvider;
import com.adniewiagmail.findme.R;
import com.parse.ParseUser;

/**
 * Created by Adaś on 2015-11-15.
 */
public class MyFriendsList extends ListActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_invites);
        MyFriendsProvider myFriendsProvider = DataManager.myFriends();
        ArrayAdapter<Friend> adapter = new ArrayAdapter<Friend>(this, R.layout.list_item_pending_invite, myFriendsProvider.getFriends());
        setListAdapter(adapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Friend friendSelected = (Friend)l.getAdapter().getItem(position);
        Intent intent = getIntentWithLocation(friendSelected.getUser());
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(MyFriendsList.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private Intent getIntentWithLocation(ParseUser user) {
        Intent intent = new Intent(MyFriendsList.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("latitude", user.getParseGeoPoint("location").getLatitude());
        intent.putExtra("longitude", user.getParseGeoPoint("location").getLongitude());
        return intent;
    }
}
