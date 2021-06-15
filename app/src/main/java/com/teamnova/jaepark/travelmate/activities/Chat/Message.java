package com.teamnova.jaepark.travelmate.activities.Chat;

import java.io.Serializable;


public class Message implements Serializable {

    //메시지 타입 정보
    private MessageType messageType;
    public enum MessageType { TALK, SYSTEM, FILE }
    TalkMessage talkMessage;
    SystemMessage systemMessage;
    FileMessage fileMessage;

    //유저 정보
    String senderID;
    int sender_idx;
    String sender_nickname;

    //채팅방 정보
    int room_idx;
    String roomID;


    String roomName;

    //통신 및 db정보
    String server_recieve_time;
    String server_msg_idx;
    String client_sendtime;
    String client_msg_idx;

    //기타 정보
    String content;

    private static final long serialVersionUID = 1L;


    public Message() { }

//    public Message(String senderID, String sender_nickname, String room_idx)
//    {
//        this.senderID = senderID;
//        this.sender_nickname = sender_nickname;
//        this.room_idx = room_idx;
//    }


    public class TalkMessage implements Serializable {

        private static final long serialVersionUID = 2L;

        static public final int TALK = 1000;
        static public final int READ = 1001;

        int talkType;
        String sender_img_uri;

        int unread_users;
        String last_read_message_idx;

        private TalkMessage() {

        }

        // Set Talk Message
        public TalkMessage setTalkType(int talkType) {

            if (talkMessage == null) {
                System.out.println("The message type value must be set first.");
                return null;
            }

            this.talkType = talkType;
            return this;
        }

        public TalkMessage setSender_img_uri(String sender_img_uri) {
            this.sender_img_uri = sender_img_uri;
            return this;
        }


        public TalkMessage setUnread_users(int unread_users) {
            this.unread_users = unread_users;
            return this;
        }

        public TalkMessage setLast_read_message_idx(String last_read_message_idx) {
            this.last_read_message_idx = last_read_message_idx;
            return this;
        }

        public Message complete()
        {
            return Message.this;
        }

    }


    public class SystemMessage implements Serializable {

        private static final long serialVersionUID = 4L;

        static public final int ENTER = 100;
        static public final int EXIT = 101;
        static public final int ALIVE = 102;
        static public final int SYNC = 103;

        int systemType;
        String last_server_msg_idx;
        String last_server_recieve_time;
        String messages;

        private SystemMessage() {

        }

        // Set SystemMessage
        public SystemMessage setSystemType(int type) {

            this.systemType = type;
            return this;
        }

        public SystemMessage setLast_server_recieve_time(String last_server_recieve_time) {
            this.last_server_recieve_time = last_server_recieve_time;
            return this;
        }

        public SystemMessage setLast_server_msg_idx(String last_server_msg_idx) {
            this.last_server_msg_idx = last_server_msg_idx;
            return this;
        }

        public void setMessages(String messages) {
            this.messages = messages;
        }

        public Message complete()
        {
            return Message.this;
        }

    }



    public class FileMessage implements Serializable {

        private static final long serialVersionUID = 3L;

        static public final int PHOTO = 2000;
        static public final int VIDEO = 2001;
        static public final int FILE = 2002;

        static public final int BEFORE_SEND = 2010;
        static public final int READY = 2011;
        static public final int SEND = 2012;
        static public final int RECIEVE = 2013;
        static public final int RESEND_REQUEST = 2014;
        static public final int RESEND = 2015;
        static public final int CANCEL_REQUEST = 2016;
        static public final int CANCEL = 2017;
        static public final int STOP = 2018;
        static public final int COMPLETE = 2019;
        static public final int BROKEN = 2020;

        int fileType;
        int state;
        int total_size;
        int current_size;
        String data;

        private FileMessage() {

        }

        public Message complete()
        {
            return Message.this;
        }
    }


    // Common


    public TalkMessage setTalkMessage() {
        talkMessage = new TalkMessage();
        messageType = MessageType.TALK;
        return talkMessage;
    }

    public SystemMessage setSystemMessage() {
        systemMessage = new SystemMessage();
        messageType = MessageType.SYSTEM;
        return systemMessage;
    }

    public FileMessage setFileMessage() {
        fileMessage = new FileMessage();
        messageType = MessageType.FILE;
        return fileMessage;
    }

    public Message setSender_idx(int sender_idx) {
        this.sender_idx = sender_idx;
        return this;
    }

    public Message setSenderID(String senderID) {
        this.senderID = senderID;
        return this;
    }

    public Message setSender_nickname(String setSender_nickname) {
        this.sender_nickname = setSender_nickname;
        return this;
    }

    public Message setRoom_idx(int room_idx) {
        this.room_idx = room_idx;
        return this;
    }

    public Message setServer_recieve_time(String server_recieve_time) {
        this.server_recieve_time = server_recieve_time;
        return this;
    }

    public Message setRoomID(String roomID) {
        this.roomID = roomID;
        return this;
    }

    public Message setRoomName(String roomName) {
        this.roomName = roomName;
        return this;
    }

    public Message setSander_nickname(String sender_nickname) {
        this.sender_nickname = sender_nickname;
        return this;
    }


    public Message setClient_sendtime(String client_sendtime) {
        this.client_sendtime = client_sendtime;
        return this;
    }

    public Message setClient_msg_idx(String client_msg_idx) {
        this.client_msg_idx = client_msg_idx;
        return this;
    }

    public Message setContent(String content) {
        this.content = content;
        return this;
    }

    public Message setServer_msg_idx(String server_msg_idx) {
        this.server_msg_idx = server_msg_idx;
        return this;
    }



    public MessageType getMessageType() { return this.messageType; }

    public String getSenderID() { return senderID; }

    public int getSender_idx() { return sender_idx; }

    public String getSender_nickname() { return sender_nickname; }

    public int getRoom_idx() { return room_idx; }

    public String getRoomID() { return roomID; }

    public String getRoomName() {
        return roomName;
    }

    public String getClient_msg_idx() { return this.client_msg_idx; }

    public String getClient_sendtime() { return this.client_sendtime; }

    public String getServer_msg_idx() { return server_msg_idx; }

    public String getContent() { return content; }




    // Get Talk

    public int getTalkType() {
        if(talkMessage != null)
            return talkMessage.talkType;
        else
            return -1;
    }

    public String getSender_img_uri() {
        if(talkMessage != null)
            return talkMessage.sender_img_uri;
        else
            return null;
    }

    public String getServer_recieve_time() {
        if(talkMessage != null)
            return server_recieve_time;
        else
            return null;
    }

    public int getUnread_users() {
        if(talkMessage != null)
            return talkMessage.unread_users;
        else
            return -1;
    }

    public String getLast_read_message_idx() {
        if(talkMessage != null)
            return talkMessage.last_read_message_idx;
        else
            return null;
    }



    // System
    public int getSystemType() {
        if(systemMessage != null)
            return systemMessage.systemType;
        else
            return -1;
    }

    public String getLast_server_recieve_time() {
        if(systemMessage != null)
            return systemMessage.last_server_recieve_time;
        else
            return null;
    }

    public String getLast_server_msg_idx() {
        if(systemMessage != null)
            return systemMessage.last_server_msg_idx;
        else
            return null;
    }

    public String getMessages() {
        if(systemMessage != null)
            return systemMessage.messages;
        else
            return null;
    }


    // File
    public int getFileType() {
        if(fileMessage != null)
            return fileMessage.fileType;
        else
            return -1;
    }

    public int getFileState() {
        if(fileMessage != null)
            return fileMessage.state;
        else
            return -1;
    }

    public int getTotal_size() {
        if(fileMessage != null)
            return fileMessage.total_size;
        else
            return -1;
    }

    public int getCurrent_size() {
        if(fileMessage != null)
            return fileMessage.current_size;
        else
            return -1;
    }

    public String getData() {
        if(fileMessage != null)
            return fileMessage.data;
        else
            return null;
    }

}