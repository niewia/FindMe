package com.adniewiagmail.findme.Activities.PendingInvites;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.adniewiagmail.findme.Activities.AddFriendPopup;
import com.adniewiagmail.findme.Activities.FindFriends;
import com.adniewiagmail.findme.Activities.FriendsList.Friend;
import com.adniewiagmail.findme.Activities.MainActivity.MainActivity;
import com.adniewiagmail.findme.Persistence.DataManager;
import com.adniewiagmail.findme.R;

/**
 * Created by Adaś on 2015-11-15.
 */
public class PendingInvitesList extends ListActivity {
    private PendingInvitesAdapter adapter;
    private Button findFriends;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_invites);
        adapter = new PendingInvitesAdapter(PendingInvitesList.this, DataManager
                .pendingInvites().getInvites());
        setListAdapter(adapter);
        findFriends = (Button) findViewById(R.id.emptyPendingInvitesButton);
        getListView().setEmptyView(findFriends);
        DataManager.pendingInvites().populateList(this);
        findFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PendingInvitesList.this, FindFriends.class);
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
        Invite invite = DataManager.pendingInvites().getInvites().get(position);
        Friend friend = invite.getFriend();
        new AddFriendPopup(this, friend);
    }

    public PendingInvitesAdapter getAdapter() {
        return adapter;
    }

    @Override
    public void onBackPressed() {
        Parcelable cameraPosition = getIntent().getParcelableExtra("cameraPosition");
        Intent intent = new Intent(PendingInvitesList.this, MainActivity.class);
        intent.putExtra("cameraPosition", cameraPosition);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
