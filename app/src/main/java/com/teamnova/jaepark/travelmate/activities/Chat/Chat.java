package com.teamnova.jaepark.travelmate.activities.Chat;

import java.sql.Timestamp;
import java.util.Date;


public class Chat {

    public final int ME = 0;
    public final int OTHERS = 1;
    public final int ENTER_ALARM = 2;
    public final int EXIT_ALARM = 3;

    int client_msg_idx;
    int server_msg_idx;
    int unread_users;

    String imgUri;
    String userID;
    String userName;
    String content;
    Timestamp timestamp;

    public int getUnread_users()
    {
        return unread_users;
    }

    public void setUnread_users(int unread_users)
    {
        this.unread_users = unread_users;
    }


    public int getClient_msg_idx()
    {
        return client_msg_idx;
    }

    public void setClient_msg_idx(int client_msg_idx)
    {
        this.client_msg_idx = client_msg_idx;
    }

    public int getServer_msg_idx()
    {
        return server_msg_idx;
    }

    public void setServer_msg_idx(int server_msg_idx)
    {
        this.server_msg_idx = server_msg_idx;
    }

    public Timestamp getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp)
    {
        this.timestamp = timestamp;
    }

    int viewType;

    public String getUserID()
    {
        return userID;
    }

    public void setUserID(String userID)
    {
        this.userID = userID;
    }

    public int getViewType()
    {
        return viewType;
    }

    public void setViewType(int type)
    {
        this.viewType = type;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public String getImgUri()
    {
        return imgUri;
    }

    public void setImgUri(String imgUri)
    {
        this.imgUri = imgUri;
    }

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }
}
