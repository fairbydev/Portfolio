package kr.teamwd.woori.Chat.Server;
import java.sql.ResultSet;
import java.util.ArrayList;


import kr.teamwd.woori.Chat.Server.DB.DBConnector;

public class Manager {

	//싱글턴 패턴
	private static Manager manager;
	private static ArrayList<ChatRoom> rooms;
	private static ArrayList<User> users;
	
	private Manager()
	{
		
		if(rooms == null)
			rooms = new ArrayList<ChatRoom>();
		
		if(users == null)
			users = new ArrayList<User>();
		
		//DB에서 방/유저 데이터 읽어 온 후 ArrayList에 셋팅
		try 
		{									
			//DB 연결		
			DBConnector dbConnector = new DBConnector();
	        dbConnector.connectToDB();
	        	        
	     	//1.유저 정보 셋팅
	        String sql = "SELECT * FROM `Belonged_Meeting` JOIN `Meeting` JOIN `user` WHERE `Belonged_Meeting`.`room_idx` = `Meeting`.`idx` AND `user`.`idx` = `Belonged_Meeting`.`user_idx` GROUP BY `Belonged_Meeting`.`user_idx`";
	        
	        JSONArray userRecodes = dbConnector.selectChatList(sql);
	        
	        for(int i=0; i<userRecodes.length(); ++i)
	        {
	        	String user_idx = userRecodes.optJSONObject(i).optString("user_idx");
	        	String user_name = userRecodes.optJSONObject(i).optString("user_name");	        	
	        		        
	        	User user = new User(user_idx, user_name);
	        	users.add(user);
	        }
	        
	        for(User user : users)
	        {
	        	System.out.println(user.getUserID()+" / "+user.getUserName()+" 유저 생성");
	        }
	        
	        //2.방 정보 셋팅	
	        sql = "SELECT * FROM `Belonged_Meeting` JOIN `Meeting` JOIN `user` WHERE `Belonged_Meeting`.`room_idx` = `Meeting`.`idx` AND `user`.`idx` = `Belonged_Meeting`.`user_idx` GROUP BY `Belonged_Meeting`.`room_idx`";
	        JSONArray roomRecodes = dbConnector.selectChatList(sql);
	        
	        for(int i=0; i<roomRecodes.length(); ++i)
	        {
	        	String room_idx = roomRecodes.optJSONObject(i).optString("room_idx");
	        	String room_name = roomRecodes.optJSONObject(i).optString("room_name");	        	
	        		        
	        	ChatRoom room = new ChatRoom(room_idx, room_name);
	        	rooms.add(room);
	        }
	
	        for(ChatRoom room : rooms)
	        {
	        	System.out.println(room.roomID+" / "+room.roomName+" 방 생성");
	        }	        	        
	        	        
	        //3.방에 유저들 입장시키기
	        sql = "SELECT * FROM `Belonged_Meeting`";
	        ResultSet resultSet = dbConnector.select(sql);
	        
	        String roomID = null;
	        int i = 0;
	        
	        while(resultSet.next())
	        {
	        	
	        	String userID = resultSet.getString("user_idx");
	        	String roomID2 = resultSet.getString("room_idx");
	        	
	        	if(roomID == null)
	        		roomID = roomID2;
	        	
	        	if(!roomID.equals(roomID2))
	        		++i;
	        	
	        	rooms.get(i).addUser(findUser(userID));
	        		        	
	        	roomID = roomID2;	        	    	
	    
	        }	        	       			
			        
	        dbConnector.close();
	        
		} 
		
		catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public static Manager getInstance()
	{
		if(manager == null)
		{						
			manager = new Manager();						
		}
		
		return manager;
	}
	
	public void addUser(User user)
	{								
		users.add(user);
		System.out.println("User Created : "+user.getUserName() + " / " + user.getUserID() + " / "+user.getUserSocket().socket.getInetAddress());
	}
	
	public void deleteUser(String userID)
	{
		for(int i=0; i<users.size(); ++i)
		{
			if(users.get(i).getUserID().equals(userID))
			{
				users.remove(i);
				System.out.println(users.get(i).getUserID()+" 유저가 삭제되었습니다.");
			}
		}
	}
	
	public void deleteUser(User user)
	{
		if(user != null)
		{
			boolean isDelete = users.remove(user);
			if(isDelete)
				System.out.println(user.getUserID()+" 유저가 삭제되었습니다.");
			
			else
				System.out.println(user.getUserID()+" 유저가 삭제에 실패하였습니다.");
		}		
	}
		
	public User findUser(String userID)
	{
		for(int i=0; i<users.size(); ++i)
		{
			if(users.get(i).getUserID().equals(userID))
			{
				return users.get(i);
			}
		}
		
		return null;
	}
	
	//방에 정상적으로 들어갔으면 true 그렇지 않으면 false 반환
	public boolean enterRoom(User user, String roomID)
	{		
		//RoomID에 일치하는 방이 존재하는지 체크
		int index;
		if((index = getIndexOfRoomID(roomID)) != -1)
		{															
			try {

				//JDBC(mysql 연결)
	            DBConnector dbConnector = new DBConnector();
	            dbConnector.connectToDB();
	            
	            //방 메시지 테이블 생성
	            String sql = "CREATE TABLE IF NOT EXISTS Chatroom_"+roomID+"_Message ( `server_msg_idx` INT NOT NULL AUTO_INCREMENT , `client_msg_idx` INT, `sender_idx` INT, `sender_name` VARCHAR(30) NOT NULL , `room_idx` INT NOT NULL , `content` TEXT NOT NULL , `client_sendtime` TIMESTAMP NOT NULL DEFAULT 0, `server_receivetime` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP , `un_read_count` INT NOT NULL , PRIMARY KEY (`server_msg_idx`), UNIQUE(`client_msg_idx`,`sender_idx`, `client_sendtime`)) ENGINE = InnoDB CHARACTER SET utf8 COLLATE utf8_unicode_ci";
	            dbConnector.update(sql);
	            
	            sql = "CREATE TABLE IF NOT EXISTS `Woori`.`Chatroom_"+roomID+"_Read_Message` ( `read_idx` INT NOT NULL AUTO_INCREMENT , `server_msg_idx` INT NOT NULL , `reader_idx` INT NOT NULL , `reader_name` VARCHAR(30) NOT NULL , PRIMARY KEY (`read_idx`), FOREIGN KEY (`reader_idx`) REFERENCES `Woori`.`user`(`idx`) ON DELETE CASCADE ON UPDATE CASCADE, FOREIGN KEY (`server_msg_idx`) REFERENCES `Woori`.`Chatroom_"+roomID+"_Message`(`server_msg_idx`) ON DELETE CASCADE ON UPDATE CASCADE) ENGINE = InnoDB CHARACTER SET utf8 COLLATE utf8_unicode_ci";
	            dbConnector.update(sql);
	            	            
	            dbConnector.close();
	            
	            //유저가 해당 방에 들어갔는지
	            if(rooms.get(index).addUser(user))
	            {
	            	return true;
	            }
	            
	            //해당 방에 유저가 이미 들어간 상태라 false 반환
	            else
	            {
	            	return false;
	            }
	            
				
			} 
			
			catch (Exception e) {
				// TODO: handle exception
				System.out.println(roomID+" 방 입장에 실패하였습니다.");
				System.out.println("실패 이유 : Exception 발생");
				return false;
			}
			
		}
		
		else
		{
			System.out.println(roomID+" 방 입장에 실패하였습니다.");
			System.out.println("실패 이유 : " +roomID+" 에 일치하는 방이 없습니다.");
			return false;
		}
	}
	
	public boolean exitRoom(User user, String roomID)
	{
		int index;
		if((index = getIndexOfRoomID(roomID)) != -1)
		{
			if(rooms.get(index).removeUser(user))
			{
				//해당 방에 아무도 없으면 방 삭제
				if(rooms.get(index).getUserCount() <= 0)
				{
					removeRoom(roomID);                
				}
				
				return true;
			}
			
			else
			{
				return false;
			}
			
		}
		
		else
		{
			System.out.println(roomID+" 방 퇴장에 실패하였습니다.");
			System.out.println("실패 이유 : "+roomID+" 에 일치하는 방이 없습니다.");
			return false;
		}
	}
	
	public boolean isExistedRoom(String roomID)
	{
		if(getIndexOfRoomID(roomID) == -1)
			return false;
		
		else
			return true;
	}
	
	public boolean createRoom(String roomID, String roomName)
	{
		if(isExistedRoom(roomID) == false)
		{
			ChatRoom room = new ChatRoom(roomID, roomName);		
			rooms.add(room);
			System.out.println(roomID+" / "+roomName+" 방이 생성 되었습니다.");	
			return true;
			
		}
		
		else
		{
			System.out.println(roomID+" / "+roomName+" 해당 방은 이미 존재하여 방 생성에 실패하였습니다.");
			return false;
		}
	}
	
	public boolean removeRoom(String roomID)
	{
		int index;
		if((index = getIndexOfRoomID(roomID)) != -1)
		{						
			//JDBC(mysql 연결)
			try
			{
				DBConnector dbConnector = new DBConnector();
                dbConnector.connectToDB();					                           
                
                String sql = "DROP TABLE Chatroom_"+roomID+"_Read_Message";
                dbConnector.update(sql);
                
                sql = "DROP TABLE Chatroom_"+roomID+"_Message";
                dbConnector.update(sql);
                                              
                dbConnector.close();
                                    			
    			System.out.println(roomID+" / "+rooms.get(index).roomName+" 방은 삭제 되었습니다.");
    			rooms.remove(index);
    			
                return true;
			}
			
			catch(Exception e)
			{
				e.printStackTrace();
				System.out.println(roomID+" / "+rooms.get(index).roomName+" 방 삭제에 실패하였습니다.");
				System.out.println("실패 이유 : Exception 발생");
				return false;
			}
		}
		
		else
		{
			System.out.println(roomID+" / "+rooms.get(index).roomName+" 방 삭제에 실패하였습니다.");
			System.out.println("실패 이유 : "+roomID+" 에 일치하는 방이 없습니다.");
			return false;
		}
				
	}
	
	public int getIndexOfRoomID(String roomID)
	{
		for(int i=0; i<rooms.size(); ++i)
		{			
			if(roomID.equals(rooms.get(i).roomID))
				return i;			
		}
		
		System.out.println(roomID+" room is not existed");
		return -1;	
	}
	
	public ChatRoom getRoom(String roomID)
	{
		int index = getIndexOfRoomID(roomID);
		if(index != -1)
		{
			return rooms.get(index);
		}
				
		return null;
	}
	
	public ArrayList<String> gatherRoomIDList(User user)
	{
		ArrayList<String> roomIDList = new ArrayList<String>();
		
		for(ChatRoom room : rooms)
		{
			if(room.findUser(user))
			{
				roomIDList.add(room.roomID);
			}
		}
		
		return roomIDList;
	}
	
}
