package kr.teamwd.woori.Chat.Server;

import java.util.ArrayList;

import kr.teamwd.woori.Chat.Message2;
import kr.teamwd.woori.Chat.Server.User.UserSocket;
import kr.teamwd.woori.Chat.Server.DB.DBConnector;

public class ChatRoom {
	
	ArrayList<User> users;
	String roomID;
	String roomName;
	
	public ChatRoom(String roomID, String roomName)
	{
		this.roomID = roomID;
		this.roomName = roomName;
		users = new ArrayList<User>();
	}
	
	public int getUserCount()
	{
		return users.size();
	}
	
	public boolean removeUser(User user)
	{					
		
		try {
			
			int index = getIndexOfUsers(user.getUserID());
			if(index != -1)
			{			
				DBConnector dbConnector = new DBConnector();
	            dbConnector.connectToDB();	
	                
	        	String sql = "DELETE FROM Belonged_Meeting WHERE room_idx="+roomID+" AND user_idx="+user.getUserID()+";";
	        	dbConnector.update(sql);
	        	
	        	dbConnector.close();
				
				users.remove(index);				
				//user.unSetOnMessageRecieveListener(this);
				System.out.println("------- room info --------");
				System.out.println("roomID : "+roomID+" / room_name : "+roomName);
				System.out.println("userID : "+user.getUserID()+" / user_name : "+user.getUserName()+"가 나갔습니다.");
				System.out.println("--------------------------");
				return true;
			}		
			
			else
			{
				System.out.println("------- 경고 --------");
				System.out.println(roomID+roomName+"에 "+user.getUserID()+" / "+user.getUserName()+" 유저가 없어 나갈 수 없습니다.");
				System.out.println("-------------------");						
				return false;
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("------- 경고 --------");
			System.out.println("roomID : "+roomID+" / room_name : "+roomName +" 방에 userID : "+user.getUserID()+" / username : "+user.getUserName()+"를 퇴장시키지 못하였습니다.");
			System.out.println("-------------------");
			return false;
		}
		
		
	}
	
	public ArrayList<User> getUserList()
	{
		return users;
	}
	
	public boolean addUser(User user)
	{	
		
		try {
			
			if(!findUser(user))
			{			
				//채팅방 테이블에 레코드 추가
	        	DBConnector dbConnector = new DBConnector();
	            dbConnector.connectToDB();	
	                
	        	String sql = "INSERT INTO Belonged_Meeting (room_idx, user_idx) VALUES ("+roomID+", "+user.getUserID()+")";
	        	dbConnector.update(sql);
	        	
	        	dbConnector.close();
				
				users.add(user);		
				//user.setOnMessageRecieveListener(this);
				System.out.println("------- room info --------");
				System.out.println("roomID : "+roomID+" / room_name : "+roomName);
				System.out.println("userID : "+user.getUserID()+" / username : "+user.getUserName()+"가 입장하였습니다.");
				return true;
			}
			
			else
			{
				System.out.println("------- 경고 --------");
				System.out.println("roomID : "+roomID+" / room_name : "+roomName +" 방에 이미 userID : "+user.getUserID()+" / username : "+user.getUserName()+"가 존재하여 입장 할 수 없습니다.");
				System.out.println("-------------------");
				return false;
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("------- 경고 --------");
			System.out.println("roomID : "+roomID+" / room_name : "+roomName +" 방에 userID : "+user.getUserID()+" / username : "+user.getUserName()+"를 추가하지 못하였습니다.");
			System.out.println("-------------------");
			return false;
		}							
	}
	
	public int getIndexOfUsers(String userID)
	{
		for(int i=0; i<users.size(); ++i)
		{
			if(userID.equals(users.get(i).getUserID()))
			{
				return i;
			}
		}
		
		System.out.println("이 방에 해당 유저는 존재 하지 않습니다.");
		return -1;		
	}
	
	public boolean findUser(User user)
	{
		
		for(int i=0; i<users.size(); ++i)
		{
			if(user.getUserID().equals(users.get(i).getUserID()))
			{
				System.out.println(roomID+" 방에서 userID : "+user.getUserID()+" 인 유저가 발견되었습니다.");
				return true;
			}
		}
		
		return false;
	}

	/*public void broadcastMsg(Message message)
	{
		
		String senderID = message.getSenderID();
		
		for(int i=0; i<users.size(); ++i)
		{															
			UserSocket userSocket = users.get(i).getUserSocket();
			
			if(userSocket != null)
			{
				if(userSocket.socket.isConnected())
				{
					userSocket.sendMessage(message);
					System.out.println("send message To userID : "+users.get(i).getUserID()+" / username : "+users.get(i).getUserName());
				}
			}																			
		}
		
	}*/
	
	public void broadcastMsg(Message2 message)
	{
		
		String senderID = message.getSender_idx();
		
		for(int i=0; i<users.size(); ++i)
		{															
			UserSocket userSocket = users.get(i).getUserSocket();
			
			if(users.get(i).getUserID().equals(senderID))
				continue;
			
			if(userSocket != null)
			{
				if(userSocket.socket.isConnected())
				{
					userSocket.sendMessage(message);
					System.out.println("send message To userID : "+users.get(i).getUserID()+" / username : "+users.get(i).getUserName());
				}
			}																			
		}
		
	}
	
}
