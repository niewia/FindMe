package com.adniewiagmail.findme.Activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.adniewiagmail.findme.Codes.ErrorCodes;
import com.adniewiagmail.findme.Persistence.DataObjects.Friend;
import com.adniewiagmail.findme.R;
import com.adniewiagmail.findme.Utils.FriendshipStatus;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.json.JSONObject;

import static android.util.Patterns.EMAIL_ADDRESS;

/**
 * Created by Adaś on 2015-11-13.
 */
public class FindFriends extends AppCompatActivity {
    private EditText enterEmail;
    private Button search;
    private TextView searchResult;
    private Button addFriend;
    private String emailAddress;
    private Friend friendToAdd;
    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);
        initComponents();
    }

    private void initComponents() {
        enterEmail = (EditText) findViewById(R.id.enterEmail);
        enterEmail.addTextChangedListener(new TextWatcher() {
                                              @Override
                                              public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                              }

                                              @Override
                                              public void onTextChanged(CharSequence s, int start, int before, int count) {
                                              }

                                              @Override
                                              public void afterTextChanged(Editable s) {
                                                  emailAddress = enterEmail.getText().toString();
                                                  if (EMAIL_ADDRESS.matcher(emailAddress).matches()) {
                                                      search.setVisibility(View.VISIBLE);
                                                  } else {
                                                      search.setVisibility(View.INVISIBLE);
                                                  }
                                                  searchResult.setText("");
                                                  addFriend.setVisibility(View.INVISIBLE);
                                              }
                                          }
        );
        search = (Button) findViewById(R.id.buttonSearchFriends);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchFriend();
            }
        });
        searchResult = (TextView) findViewById(R.id.textFriendSearchResult);
        addFriend = (Button) findViewById(R.id.buttonAddFriend);
        addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFriend();
            }
        });
    }

    private void searchFriend() {
        if (isSelfInvitation()) {
            searchResult.setText(enterEmail.getText().toString() + getString(R.string.youEnteredYourEmail));
            return;
        }
        progress = new ProgressDialog(this);
        progress.show();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("_User");
        query.whereEqualTo("email", emailAddress);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                progress.dismiss();
                if (e == null) {
                    friendToAdd = new Friend(parseObject);
                    searchResult.setText(friendToAdd.toString());
//                    if (isUserAlreadyYourFriend()) {
//                        searchResult.setText(friendToAdd.toString() + getString(R.string.userAlreadyYourFriend));
//                    } else {
                    addFriend.setVisibility(View.VISIBLE);
//                    }
                } else {
                    searchResult.setText("@string/errorFriendWithEmailNotFound");
                }
            }
        });
    }

    private void addFriend() {
//        if (isUserInvitedYou()) {
//            AddFriendPopup addFriendPopup = new AddFriendPopup(this, friendToAdd, " wysłał/a Ci już zaproszenie.");
//            return;
//        }
        progress.show();
        ParseObject invitation = new ParseObject("FriendInvitation");
        invitation.put("userFrom", ParseUser.getCurrentUser());
        invitation.put("userTo", friendToAdd.getUser());
        invitation.put("status", FriendshipStatus.PENDING);
        invitation.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    searchResult.setText(friendToAdd.toString() + getString(R.string.friendInvitationSentSuccessfully));
                } else {
                    handleError(e);
                }
                progress.dismiss();
            }
        });
    }

    private void handleError(ParseException e) {
        String textToSet = "";
        int errorCode;
        try {
            JSONObject mainObject = new JSONObject(e.getMessage());
            errorCode = mainObject.getInt("code");
        } catch (JSONException e1) {
            errorCode = -1;
        }
        switch (errorCode) {
            case -1:
                textToSet = getString(R.string.addFriendErrorParseJSON);
                break;
            case ErrorCodes.USER_ALREADY_INVITED:
                textToSet = getString(R.string.userAlreadyGotAnInvitation);
                break;
            case ErrorCodes.USER_INVITED_YOU:
                AddFriendPopup addFriendPopup = new AddFriendPopup(FindFriends.this, friendToAdd, " wysłał/a " +
                        "Ci już " +
                        "zaproszenie.");
                break;
            case ErrorCodes.USER_ALREADY_YOUR_FRIEND:
                textToSet = getString(R.string.userAlreadyYourFriend);
                break;
            default:
                textToSet = getString(R.string.inviteFriendUnexpectedError);
        }
        searchResult.setText(friendToAdd.toString() + textToSet);
    }

    private boolean isSelfInvitation() {
        if (enterEmail.getText().toString().equals(ParseUser.getCurrentUser().getEmail())) {
            return true;
        }
        return false;
    }
}
