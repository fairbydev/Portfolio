package kr.teamwd.woori.Chat.Server.DB;

import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.json.JSONArray;
import org.json.JSONObject;

public class DBConnector implements Closeable {
	
	int connection_status = 0;
	final int NOTCONNECTED = 0;
	final int CONNECTED = 1;
	private Connection conn;
	private Statement stmt;
	private ResultSet rs;
		
	public Connection connectToDB()
	{
		conn = null;

		String host = "jdbc:mysql://localhost:3306/Woori?useUnicode=true&characterEncoding=utf8";
		String id = "root";
		String pw = "pjh0831!";

		try {
			Class.forName("com.mysql.jdbc.Driver");
			//System.out.println("JDBC Driver OK!!");
			conn = DriverManager.getConnection(host,id,pw);
			//System.out.println("Conn OK!!");
			connection_status = CONNECTED;
			this.stmt = conn.createStatement();
			return conn;
		}

		catch(Exception e) {
		
			e.printStackTrace();
			System.out.println("DB Connect Error");
			return null;
		}
	}
	
	public void update(String sql)
	{
		try
		{
			stmt.executeUpdate(sql);
		}
		
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public int selectNumRows(String sql)
	{
		int count = 0;
		try
		{
			rs = stmt.executeQuery(sql);
			while(rs.next())
			{ 
				count++;
			} 
			rs.close(); 
			
		}
		
		catch(Exception e)
		{
			e.printStackTrace();			
		}
		
		return count;
	}
		
	public JSONArray selectChatList(String sql)
	{
		JSONArray recodes = new JSONArray();
        JSONObject recode = null;
        int count = 0;
        try
        {
        	rs = stmt.executeQuery(sql);
            
            while (rs.next())
            {            	
                int room_idx = rs.getInt("room_idx");
                int user_idx = rs.getInt("user_idx");       
                String attend_time = rs.getString("attend_time");
                String room_name = rs.getString("meeting_name");
                String user_name = rs.getString("username");  
                                
                recode = new JSONObject();
                recode.put("room_idx", room_idx);
                recode.put("user_idx", user_idx);
                recode.put("attend_time", attend_time);
                recode.put("room_name", room_name);
                recodes.put(count, recode);
                recode.put("user_name", user_name);
                count++;
            }

            return recodes;
        }

        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
	}
	
	public JSONArray selectMsgTable(String sql)
	{
		JSONArray recodes = new JSONArray();
        JSONObject recode = null;
        int count = 0;
        try
        {
        	rs = stmt.executeQuery(sql);
            
            while (rs.next())
            {
                int server_msg_idx = rs.getInt(1);
                int client_msg_idx = rs.getInt(2);
                String sender_idx = rs.getString(3);
                String sender_name = rs.getString(4);
                int room_idx = rs.getInt(5);
                String content = rs.getString(6);
                String client_sendtime = rs.getString(7);
                String server_receivetime = rs.getString(8);
                int un_read_count = rs.getInt(9);
                
                recode = new JSONObject();
                recode.put("server_msg_idx", server_msg_idx);
                recode.put("client_msg_idx", client_msg_idx);
                recode.put("sender_idx", sender_idx);
                recode.put("sender_name", sender_name);
                recode.put("room_idx", room_idx);
                recode.put("content", content);
                recode.put("client_sendtime", client_sendtime);
                recode.put("server_receivetime", server_receivetime);
                recode.put("un_read_count", un_read_count);

                recodes.put(count, recode);
                count++;
            }

            return recodes;
        }

        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
	}
	
	public ResultSet select(String sql)
	{
		try
		{
			rs = stmt.executeQuery(sql);					
			
			/*StringBuffer sb = new StringBuffer();
			
			while(rs.next())
			{ 
				String result = ""; 
				sb.append(result);
			    //System.out.println("이름 : " + rs.getString("name") + "\t나이 : " + rs.getString(3) + "\t주소 : " + rs.getString("addr"));
			} 
			rs.close();*/
			
			return rs;
			
		}
		
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
		
	}

	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub
		try {
			stmt.close();
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
		
}
