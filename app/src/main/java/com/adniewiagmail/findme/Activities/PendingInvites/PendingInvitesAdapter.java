package com.adniewiagmail.findme.Activities.PendingInvites;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.adniewiagmail.findme.Activities.FriendsList.Friend;
import com.adniewiagmail.findme.R;

import java.util.List;

/**
 * Created by Adaś on 2015-12-21.
 */
public class PendingInvitesAdapter extends BaseAdapter{

    private final List<Invite> invites;
    private static LayoutInflater inflater=null;

    public PendingInvitesAdapter(Activity activity, List<Invite> invites) {
        this.invites = invites;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return invites.size();
    }

    @Override
    public Object getItem(int position) {
        return invites.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        if(convertView==null)
            vi = inflater.inflate(R.layout.list_item_pending_invite, null);

        TextView nickname = (TextView)vi.findViewById(R.id.inviteItemNickname);
        TextView fullname = (TextView)vi.findViewById(R.id.inviteItemFullName);
        ImageView photo =(ImageView)vi.findViewById(R.id.inviteItemPhoto);

        Invite invite = invites.get(position);
        Friend friend = invite.getFriend();

        // Setting all values in listview
        nickname.setText(friend.getUsername());
        fullname.setText(friend.toString());
        photo.setImageBitmap(friend.getProfilePhoto());
        return vi;
    }

}
