package com.teamnova.jaepark.travelmate.activities.ChatList;

import java.sql.Timestamp;


public class ChatList {

    String place;
    String hostNickname;
    String senderID;
    String senderName;
    String talkContent;
    Timestamp serverReceiveTime;

    String travelInfo;

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getHostNickname() {
        return hostNickname;
    }

    public void setHostNickname(String hostNickname) {
        this.hostNickname = hostNickname;
    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getTalkContent() {
        return talkContent;
    }

    public void setTalkContent(String talkContent) {
        this.talkContent = talkContent;
    }

    public Timestamp getServerReceiveTime() {
        return serverReceiveTime;
    }

    public void setServerReceiveTime(Timestamp serverReceiveTime) {
        this.serverReceiveTime = serverReceiveTime;
    }

    public String getTravelInfo() { return travelInfo; }

    public void setTravelInfo(String travelInfo) { this.travelInfo = travelInfo; }


}
