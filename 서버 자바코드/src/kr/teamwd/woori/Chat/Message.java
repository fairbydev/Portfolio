package kr.teamwd.woori.Chat;

import java.io.Serializable;

public class Message implements Serializable {

	String msgID;
	String senderID;
	String toRoomID;
	String content;
	String action;
	
	public Message()
	{
		
	}
	
	public Message(String msgID, String senderID, String toRoomID, String content)
	{
		this.msgID = msgID;
		this.senderID = senderID;
		this.toRoomID = toRoomID;
		this.content = content;			
	}
	
	public String getAction()
	{
	    return action;
	}

	public void setAction(String action)
	{
	    this.action = action;
	}
	
	public String getSenderID()
	{
		return senderID;
	}
	
	public void setSenderID(String senderID)
	{
		this.senderID = senderID;
	}
	
	public String getToRoomID()
	{
		return toRoomID;
	}
	
	public void setToRoomID(String roomID)
	{
		this.toRoomID = roomID;
	}
	
	public String getMsgID()
	{
		return msgID;
	}
	
	public void setMsgID(String msgID)
	{
		this.msgID = msgID;
	}
	
	public String getContent()
	{
		return content;
	}
	
	public void setContent(String content)
	{
		this.content = content;
	}
	
}

