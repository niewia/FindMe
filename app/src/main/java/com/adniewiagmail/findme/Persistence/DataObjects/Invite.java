package com.adniewiagmail.findme.Persistence.DataObjects;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * Created by Adaś on 2015-11-14.
 */
public class Invite {

    private String id;
    private ParseUser userFrom;
    private Number status;

    public Invite(ParseObject inviteObject) {
        setId(inviteObject.getObjectId());
        setStatus(inviteObject.getNumber("status"));
        try {
            setUserFrom(inviteObject.getParseUser("userFrom").fetchIfNeeded());
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ParseUser getUserFrom() {
        return userFrom;
    }

    public void setUserFrom(ParseUser userFrom) {
        this.userFrom = userFrom;
    }

    public Number getStatus() {
        return status;
    }

    public void setStatus(Number status) {
        this.status = status;
    }

    public Friend getFriend() {
        return new Friend(getUserFrom());
    }

    @Override
    public String toString(){
        return userFrom.getString("firstName") + " " + userFrom.getString("lastName");
    }
}
