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
import com.adniewiagmail.findme.Persistence.DataObjects.Invite;
import com.adniewiagmail.findme.R;

/**
 * Created by Adaś on 2015-11-15.
 */
public class PendingInvitesList extends ListActivity {

    private ArrayAdapter<Invite> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_invites);
        adapter = new ArrayAdapter<Invite>(this, R.layout.list_item_pending_invite, DataManager
                .pendingInvites().getInvites());
        setListAdapter(adapter);
        DataManager.pendingInvites().populateList(this);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Invite invite = DataManager.pendingInvites().getInvites().get(position);
        Friend friend = invite.getFriend();
        new AddFriendPopup(this, friend);
    }

    public ArrayAdapter<Invite> getAdapter() {
        return adapter;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(PendingInvitesList.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
