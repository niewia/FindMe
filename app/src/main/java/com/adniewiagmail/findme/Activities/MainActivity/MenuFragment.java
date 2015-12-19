package com.adniewiagmail.findme.Activities.MainActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.adniewiagmail.findme.Activities.EditProfile;
import com.adniewiagmail.findme.Activities.FindFriends;
import com.adniewiagmail.findme.Activities.LoginActivity;
import com.adniewiagmail.findme.Activities.MyFriendsList;
import com.adniewiagmail.findme.Activities.PendingInvitesList;
import com.adniewiagmail.findme.Activities.UpdateStatusPopup;
import com.adniewiagmail.findme.R;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

/**
 * Created by Adaś on 2015-11-12.
 */
public class MenuFragment extends Fragment {

    private Button pendingInvites;
    private Button updateStatus;
    private Button editProfile;
    private Button findFriends;
    private Button myFriends;
    private Button quit;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_main_menu, container,
                false);
        initButtons(v);
        return v;
    }

    private void initButtons(View v) {
        pendingInvites = (Button) v.findViewById(R.id.buttonPendingInvites);
        pendingInvites.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pendingInvites();
                    }
                }
        );

        myFriends = (Button) v.findViewById(R.id.buttonMyFriends);
        myFriends.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        myFriends();
                    }
                }
        );

        updateStatus = (Button) v.findViewById(R.id.buttonUpdateStatus);
        updateStatus.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        updateStatus();
                    }
                }
        );

        quit = (Button) v.findViewById(R.id.buttonQuit);
        quit.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        quit();
                    }
                }
        );

        findFriends = (Button) v.findViewById(R.id.buttonFindFriends);
        findFriends.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        findFriends();
                    }
                }
        );

        editProfile = (Button) v.findViewById(R.id.buttonEditProfile);
        editProfile.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editProfile();
                    }
                }
        );
    }

    private void editProfile() {
        Intent intent = new Intent(getActivity(), EditProfile.class);
        startActivity(intent);
    }

    private void myFriends() {
        Intent intent = new Intent(getActivity(), MyFriendsList.class);
        startActivity(intent);
    }

    private void pendingInvites() {
        Intent intent = new Intent(getActivity(), PendingInvitesList.class);
        startActivity(intent);
    }

    private void quit() {
        final ProgressDialog progress = new ProgressDialog(getActivity());
//        BackgroundThreadsManager.stopAll();
        progress.setMessage(getActivity().getString(R.string.loggingOutInProgress));
        progress.show();
        ParseUser currentUser = ParseUser.getCurrentUser();
        currentUser.remove("installation");
        currentUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                progress.dismiss();
                logout(progress);
            }
        });
    }

    private void logout(final ProgressDialog progress) {
        ParseUser.logOutInBackground(new LogOutCallback() {
            @Override
            public void done(ParseException e) {
                progress.dismiss();
                loadLoginView();
//                getActivity().finish();
//                System.exit(0);
            }
        });
    }

    private void loadLoginView() {
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void updateStatus() {
        new UpdateStatusPopup(getActivity());
    }

    private void findFriends() {
        Intent intent = new Intent(getActivity(), FindFriends.class);
        startActivity(intent);
    }

}
