package kr.teamwd.woori.Chat;

import java.io.Serializable;

public class Message2 implements Serializable {

    private static final long serialVersionUID = 1L;

    public enum MessageType {
        TALK, FILE, SYSTEM
    }

    public class TalkMessage implements Serializable {

        private static final long serialVersionUID = 2L;

        static public final int TALK = 1000;
        static public final int READ = 1001;

        int talkType;
        String content;
        String sender_img_uri;
        String client_msg_idx;
        String server_msg_idx;

        int unread_users;
        String last_read_message_idx;

        private TalkMessage() {

        }

        // Set Talk Message
        public Message2.TalkMessage setTalkType(int talkType) {

            if (talkMessage == null) {
                System.out.println("The message type value must be set first.");
                return null;
            }

            this.talkType = talkType;
            return this;
        }

        public Message2.TalkMessage setContent(String content) {
            this.content = content;
            return this;
        }

        public Message2.TalkMessage setSender_img_uri(String sender_img_uri) {
            this.sender_img_uri = sender_img_uri;
            return this;
        }

        public Message2.TalkMessage setClient_msg_idx(String client_msg_idx) {
            this.client_msg_idx = client_msg_idx;
            return this;
        }

        public Message2.TalkMessage setServer_msg_idx(String server_msg_idx) {
            this.server_msg_idx = server_msg_idx;
            return this;
        }

        public Message2.TalkMessage setUnread_users(int unread_users) {
            this.unread_users = unread_users;
            return this;
        }

        public Message2.TalkMessage setLast_read_message_idx(String last_read_message_idx) {
            this.last_read_message_idx = last_read_message_idx;
            return this;
        }

        public Message2 complete()
        {
            return Message2.this;
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

        public Message2 complete()
        {
            return Message2.this;
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
        public Message2.SystemMessage setSystemType(int type) {

            this.systemType = type;
            return this;
        }

        public Message2.SystemMessage setLast_server_recieve_time(String last_server_recieve_time) {
            this.last_server_recieve_time = last_server_recieve_time;
            return this;
        }

        public Message2.SystemMessage setLast_server_msg_idx(String last_server_msg_idx) {
            this.last_server_msg_idx = last_server_msg_idx;
            return this;
        }

        public void setMessages(String messages) {
            this.messages = messages;
        }

        public Message2 complete()
        {
            return Message2.this;
        }

    }

    MessageType messageType;
    TalkMessage talkMessage;
    FileMessage fileMessage;
    SystemMessage systemMessage;
    String sender_idx;
    String sender_name;
    String room_idx;
    String room_name;
    String server_recieve_time;

    // Common
    public Message2.FileMessage setFileMessage() {
        fileMessage = new FileMessage();
        messageType = Message2.MessageType.FILE;
        return fileMessage;
    }

    public Message2.TalkMessage setTalkMessage() {
        talkMessage = new TalkMessage();
        messageType = Message2.MessageType.TALK;
        return talkMessage;
    }

    public Message2.SystemMessage setSystemMessage() {
        systemMessage = new SystemMessage();
        messageType = Message2.MessageType.SYSTEM;
        return systemMessage;
    }

    public Message2 setSender_idx(String sender_idx) {
        this.sender_idx = sender_idx;
        return this;
    }

    public Message2 setSender_name(String sender_name) {
        this.sender_name = sender_name;
        return this;
    }

    public Message2 setRoom_idx(String room_idx) {
        this.room_idx = room_idx;
        return this;
    }

    public Message2 setServer_recieve_time(String server_recieve_time) {
        this.server_recieve_time = server_recieve_time;
        return this;
    }

    public Message2 setRoom_name(String room_name) {
        this.room_name = room_name;
        return this;
    }

    public MessageType getMessageType() {
        return this.messageType;
    }

    public String getSender_idx() {
        return sender_idx;
    }

    public String getSender_name() {
        return sender_name;
    }

    public String getRoom_idx() {
        return room_idx;
    }

    public String getRoom_name() {
        return room_name;
    }

    // Get Talk
    public String getContent() {
        if(talkMessage != null)
            return talkMessage.content;
        else
            return null; 
    }

    public int getTalkMessageType() {
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

    public String getClient_msg_idx() {
        if(talkMessage != null)
            return talkMessage.client_msg_idx;
        else
            return null;
    }

    public String getServer_msg_idx() {
        if(talkMessage != null)
            return talkMessage.server_msg_idx;
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

}