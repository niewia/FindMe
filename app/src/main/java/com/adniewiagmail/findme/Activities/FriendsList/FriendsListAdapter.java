package com.adniewiagmail.findme.Activities.FriendsList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.adniewiagmail.findme.R;

import java.util.List;

/**
 * Created by Adaś on 2015-12-21.
 */
public class FriendsListAdapter extends BaseAdapter{
    private final List<Friend> friends;
    private static LayoutInflater inflater=null;

    public FriendsListAdapter(Activity activity, List<Friend> friends) {
        this.friends = friends;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return friends.size();
    }

    @Override
    public Object getItem(int position) {
        return friends.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        if(convertView==null)
            vi = inflater.inflate(R.layout.list_item_my_friend, null);

        TextView name = (TextView)vi.findViewById(R.id.friendItemName);
        TextView status = (TextView)vi.findViewById(R.id.friendItemStatus);
        TextView address = (TextView)vi.findViewById(R.id.friendItemAddress);
        ImageView photo =(ImageView)vi.findViewById(R.id.friendItemPhoto);

        Friend friend = friends.get(position);

        // Setting all values in listview
        name.setText(friend.toString());
        status.setText(friend.getStatus());
        address.setText(friend.getAddress().getLocality());
        photo.setImageBitmap(friend.getProfilePhoto());
        return vi;
    }

}
