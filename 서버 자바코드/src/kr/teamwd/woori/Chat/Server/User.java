package kr.teamwd.woori.Chat.Server;

import java.io.IOException;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import org.json.JSONArray;
import org.json.JSONObject;

import kr.teamwd.woori.Chat.Message;
import kr.teamwd.woori.Chat.Message2;
import kr.teamwd.woori.Chat.Server.DB.DBConnector;

public class User {

	String userName;
	String userID;
	UserSocket userSocket;

	public User(String userID, String userName) {
		this.userID = userID;
		this.userName = userName;
	}

	public User(Socket socket, ObjectInputStream input, ObjectOutputStream output) {

		userSocket = new UserSocket(socket, input, output);

	}

	public User(String userName, String userID, Socket socket, ObjectInputStream input, ObjectOutputStream output) {
		this.userName = userName;
		this.userID = userID;

		userSocket = new UserSocket(socket, input, output);

	}

	class UserSocket {
		Socket socket;
		ObjectInputStream input;
		ObjectOutputStream output;
		SendThread send;
		ReceiveThread receive;

		public UserSocket(Socket socket, ObjectInputStream input, ObjectOutputStream output) {
			this.socket = socket;
			this.input = input;
			this.output = output;

			receive = new ReceiveThread();
			receive.start();
		}

		// send msg to client
		public void sendMessage(Message2 msg) {
			send = new SendThread(msg);
			send.start();
		}

		public void killThread() {
			if (receive.isAlive())
				receive.interrupt();

			if (send.isAlive())
				send.interrupt();
		}

		public Socket getSocket() {
			return socket;
		}

		public void close() throws IOException {
			socket.close();
		}

		class SendThread extends Thread {

			//Message sendMsg;
			Message2 sendMsg;

			public SendThread(Message2 msg) {
				sendMsg = msg;
			}

			@Override
			public void run() {

				try {
					while (socket.isConnected()) {
						
						synchronized (output) {
									
							//Sendmsg Log
							System.out.println("-----send msg-----");
							Message2.MessageType messageType = sendMsg.getMessageType();
							System.out.println("MessageType : "+messageType);
							String senderID = sendMsg.getSender_idx();
							System.out.println("SenderID : " + senderID);
							String senderName = sendMsg.getSender_name();
							System.out.println("SenderName : " + senderName);
							
							if(messageType == Message2.MessageType.SYSTEM)
							{								
								int systemType = sendMsg.getSystemType();
																						
								switch(systemType)
								{									
									case Message2.SystemMessage.ALIVE:
										
										System.out.println("System Type : ALIVE");									
										break;
								
									case Message2.SystemMessage.ENTER:
										
										System.out.println("System Type : ENTER");
										String roomID = sendMsg.getRoom_idx();
										System.out.println("RoomID : " + roomID);										
										break;
										
									case Message2.SystemMessage.EXIT:
										
										System.out.println("System Type : EXIT");
										roomID = sendMsg.getRoom_idx();
										System.out.println("RoomID : " + roomID);
										break;
										
									case Message2.SystemMessage.SYNC:																				
										
										System.out.println("System Type : SYNC");
										break;																																																																
																																											
									default:
										
										System.out.println("System Type is invalid");
										break;
								}
								
							}
							
							else if(messageType == Message2.MessageType.FILE)
							{
								
							}
							
							else
							{
								int talkType = sendMsg.getTalkMessageType();
								String roomID = sendMsg.getRoom_idx();							
								
								switch(talkType)
								{
									case Message2.TalkMessage.TALK:
															
										System.out.println("Talk Type : TALK");
										System.out.println("RoomID : " + roomID);
										String content = sendMsg.getContent();
										System.out.println("Content : " + content);
										String client_msg_idx = sendMsg.getClient_msg_idx();
										System.out.println("Client_msg_idx : " + client_msg_idx);									
										String server_msg_idx = sendMsg.getServer_msg_idx();
										System.out.println("Server_msg_idx : " + server_msg_idx);
										String server_recieve_time = sendMsg.getServer_recieve_time();
										System.out.println("Server_msg_idx : " + server_recieve_time);
										String sender_img_uri = sendMsg.getSender_img_uri();
										System.out.println("Sender_img_uri : " + sender_img_uri);
										break;
									
									case Message2.TalkMessage.READ:
																				
										System.out.println("Talk Type : READ");									
										System.out.println("RoomID : " + roomID);										
										break;
																																
								}
																																					
							}
							
							System.out.println("------------------");
							
							//Message Send
							output.writeObject(sendMsg);
							output.flush();
							break;
						}
					}

				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}

		class ReceiveThread extends Thread {

			public ReceiveThread() {
			}

			@Override
			public void run() {

				Manager manager = Manager.getInstance();

				try {
					
					while (socket.isConnected()) {
						
						synchronized(input)
						{
							Message2 msg = (Message2) input.readObject();
							System.out.println("-----recieve msg-----");
							
							if(msg != null)
							{
								Message2.MessageType messageType = msg.getMessageType();
								System.out.println("Message Type : "+messageType);                               
                                String senderID = msg.getSender_idx();
                                System.out.println("SenderID : " + senderID);
                                String senderName = msg.getSender_name();
                                System.out.println("SenderName : " + senderName);
								
								switch(messageType)
								{
									case SYSTEM:																	
										
										int systemType = msg.getSystemType();
										
										switch(systemType)
										{
											case Message2.SystemMessage.ALIVE:
												
												System.out.println("System Type : ALIVE");
												sendMessage(msg);
												break;
												
											case Message2.SystemMessage.ENTER:
																			
												System.out.println("System Type : ENTER");
												String roomName = msg.getRoom_name();
												String roomID = msg.getRoom_idx();
												
												//채팅방 유무 체크
												if(!manager.isExistedRoom(roomID))
												{
													manager.createRoom(roomID, roomName);
													manager.enterRoom(User.this, roomID);
												}
													
												//채팅방 입장
												else
												{
													manager.enterRoom(User.this, roomID);
													sendMsgToRoom(roomID, msg);
												}
												
												//클라이언트에게 방 생성 완료 전송												
												//msg.setServer_recieve_time();																						
												sendMessage(msg);
												break;
												
											case Message2.SystemMessage.EXIT:
												
												System.out.println("System Type : EXIT");
												roomName = msg.getRoom_name();
												roomID = msg.getRoom_idx();
												
												if(manager.isExistedRoom(roomID))
												{
													manager.exitRoom(User.this, roomID);
													sendMsgToRoom(roomID, msg);
												}
												
												sendMessage(msg);
												break;
												
											case Message2.SystemMessage.SYNC:
												
												System.out.println("System Type : SYNC");
												
												break;
																																			
										}
										
										
										break;
										
									case TALK:
										
										break;
									
									case FILE:
										
										int fileType = msg.getFileType();	                                    

	                                    switch (fileType)
	                                    {
	                                        case Message2.FileMessage.PHOTO:

	                                            break;

	                                        case Message2.FileMessage.VIDEO:

	                                            break;

	                                        case Message2.FileMessage.FILE:

	                                            break;
	                                    }
										break;
										
									default:
										
										System.out.println("Type is null");
										break;									
								}
							
							}
							
							else
							{
								System.out.println("msg is null");
							}
							
							System.out.println("------------------");
						}
												
						//Message msg = (Message) input.readObject();
						

						/*if (msg != null) {
							
							String action = msg.getAction();
							String content = msg.getContent();
							String senderID = msg.getSenderID();
							String toRoomID = msg.getToRoomID();
							System.out.println("-----recieve msg-----");
							System.out.println("Action : " + action);
							System.out.println("Content : " + content);
							System.out.println("SenderID : " + senderID);
							System.out.println("ToRoomID : " + toRoomID);

							if (action != null) {
								switch (action) {

								case "alive":

									sendMessage(msg);
									break;

								case "sync":

									msg = syncMessage(msg);
									if (msg != null) {
										sendMsgToRoom(toRoomID, msg);
									}

									else {
										System.out.println("syncMsg is null");
									}

									break;

								case "read_msg":

									msg = saveReadMessage(msg);

									if (msg != null) {
										sendMsgToRoom(toRoomID, msg);
									}

									else {
										System.out.println("readMsg is null");
									}

									break;

								case "create_room":

									JSONObject object = new JSONObject(content);
									String roomName = object.getString("roomname");
									String createRoomID = object.getString("create_roomID");

									manager.createRoom(createRoomID, roomName);

									// 생성된 방 ID 클라이언트에게 전송
									msg = new Message();
									msg.setAction("created");
									msg.setSenderID("server");

									object = new JSONObject();
									object.put("created_roomID", toRoomID);
									msg.setContent(object.toString());

									sendMessage(msg);

									break;

								case "enter_room":

									if (manager.enterRoom(User.this, toRoomID)) {
										msg.setAction("entered_room");
										sendMsgToRoom(toRoomID, msg);

									}

									else {
										msg.setAction("entered_fail");
										sendMessage(msg);
									}

									break;

								case "exit_room":

									if (manager.exitRoom(User.this, toRoomID)) {
										msg.setAction("exited_room");
										sendMsgToRoom(toRoomID, msg);
									}

									else {

									}

									break;

								case "talk":

									if (manager.getRoom(toRoomID) != null) {
										// 메시지 서버에 저장 후 방에 메시지 전송
										String sender_idx = senderID;
										String room_idx = toRoomID;

										JSONObject data = new JSONObject(content);

										String sender_name = data.optString("username");
										String talk_content = data.optString("talk");
										String client_sendtime = data.optString("client_sendtime");
										String client_msg_idx = data.optString("client_msg_idx");

										// 해당 방에 속한 유저 수
										manager = Manager.getInstance();
										int un_read_count = manager.getRoom(room_idx).getUserCount() - 1;

										DBConnector dbConnector = new DBConnector();
										dbConnector.connectToDB();

										// 메시지 저장
										String sql = "INSERT INTO Chatroom_" + room_idx
												+ "_Message (`client_msg_idx`, `sender_idx`, `sender_name`, `room_idx`, `content`, `client_sendtime`, `un_read_count`) VALUES ( "
												+ client_msg_idx + ", " + sender_idx + ", '" + sender_name + "', "
												+ room_idx + ", '" + talk_content + "', '" + client_sendtime + "',"
												+ un_read_count + " )";
										dbConnector.update(sql);

										// server_msg_idx, server_receivetime
										sql = "SELECT * FROM Chatroom_" + room_idx
												+ "_Message ORDER BY server_msg_idx DESC LIMIT 1";
										JSONArray recodes = dbConnector.selectMsgTable(sql);
										String server_msg_idx = recodes.getJSONObject(0).getString("server_msg_idx");
										String server_receivetime = recodes.getJSONObject(0)
												.getString("server_receivetime");
										un_read_count = recodes.getJSONObject(0).getInt("un_read_count");
										dbConnector.close();

										// 메세지에 server_msg_idx,
										// server_receivetime 추가
										data.put("server_msg_idx", server_msg_idx);
										data.put("server_receivetime", server_receivetime);
										data.put("un_read_count", un_read_count);
										msg.setContent(data.toString());

										// 방에 메세지 전송
										sendMsgToRoom(toRoomID, msg);
									}

									break;

								default:

									break;

								}

							}

						}

						else {
							System.out.println("msg is null");
						}*/
					}

					System.out.println(userID + "/" + userName + " Socket disconnected");
					socket.close();

				}

				catch (Exception e) {
					e.printStackTrace();

				}

			}

		}

	}

	public Message saveReadMessage(Message msg) {

		try {

			DBConnector dbConnector = new DBConnector();
			dbConnector.connectToDB();

			String reader_idx = msg.getSenderID();
			String roomID = msg.getToRoomID();

			JSONObject data = new JSONObject(msg.getContent());
			String reader_name = data.getString("reader_name");
			// String reader_idx = data.getString("reader_idx");
			String str_messages = data.getString("messages");
			JSONArray messages = new JSONArray(str_messages);

			Message newMsg = new Message();
			newMsg.setAction("read_msg");
			newMsg.setSenderID(reader_idx);
			newMsg.setToRoomID(roomID);

			JSONArray recodes = new JSONArray();
			for (int i = 0; i < messages.length(); ++i) {
				int server_msg_idx = messages.getJSONObject(i).optInt("server_msg_idx");
				// 이미 읽었던 적이 있는지 체크
				String sql = "SELECT * FROM Chatroom_" + roomID + "_Read_Message WHERE server_msg_idx = "
						+ server_msg_idx + " AND reader_idx = " + reader_idx + "";
				if (dbConnector.selectNumRows(sql) < 1) {
					// 데이터 추가
					sql = "REPLACE INTO Chatroom_" + roomID
							+ "_Read_Message (server_msg_idx, reader_idx, reader_name) VALUES (" + server_msg_idx + ", "
							+ reader_idx + ", '" + reader_name + "')";
					dbConnector.update(sql);

					// un_read_count -1 감소
					sql = "UPDATE Chatroom_" + roomID
							+ "_Message SET `un_read_count`=`un_read_count`-1 WHERE server_msg_idx=" + server_msg_idx
							+ " AND un_read_count > 0";
					dbConnector.update(sql);

					sql = "SELECT * FROM Chatroom_" + roomID + "_Message WHERE server_msg_idx=" + server_msg_idx + "";
					int un_read_count = dbConnector.selectMsgTable(sql).getJSONObject(0).optInt("un_read_count");

					JSONObject recode = new JSONObject();
					recode.put("un_read_count", un_read_count);
					recode.put("server_msg_idx", server_msg_idx);

					recodes.put(recode);
				}
			}

			newMsg.setContent(recodes.toString());

			dbConnector.close();
			return newMsg;
		}

		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return null;
		}

	}

	public Message syncMessage(Message msg) {
		try {
			// 클라이언트로 부터 전송 받은 메시지 서버에 저장
			saveNewMessage(msg);

			// 클라이언트가 못 받은 메시지 및 방금 전에 보낸 메세지 클라이언트에게 재전송
			JSONObject data = new JSONObject(msg.getContent());
			String last_receive_time = data.getString("last_receive_time");
			String sender_idx = msg.getSenderID();

			DBConnector dbConnector = new DBConnector();
			dbConnector.connectToDB();

			String roomID = msg.getToRoomID();

			String sql = "SELECT * FROM Chatroom_" + roomID
					+ "_Message WHERE server_receivetime <= TIMESTAMP(NOW()) AND server_receivetime > TIMESTAMP('"
					+ last_receive_time + "')";
			JSONArray recodes = dbConnector.selectMsgTable(sql);

			Message newMsg = new Message();
			newMsg.setAction("sync");
			newMsg.setSenderID(sender_idx);
			newMsg.setToRoomID(roomID);
			newMsg.setContent(recodes.toString());

			dbConnector.close();
			return newMsg;

		}

		catch (Exception e) {
			return null;
		}

	}

	public void saveNewMessage(Message msg) {
		// DB연결
		DBConnector dbConnector = new DBConnector();

		try {

			dbConnector.connectToDB();

			JSONObject data = new JSONObject(msg.getContent());
			JSONArray messages = new JSONArray(data.getString("messages"));

			for (int i = 0; i < messages.length(); ++i) {
				String client_msg_idx = messages.getJSONObject(i).getString("client_msg_idx");
				String sender_idx = messages.getJSONObject(i).getString("sender_idx");
				String sender_name = messages.getJSONObject(i).getString("sender_name");
				String room_idx = messages.getJSONObject(i).getString("room_idx");
				String talk_content = messages.getJSONObject(i).getString("content");
				String client_sendtime = messages.getJSONObject(i).getString("client_sendtime");

				// 해당 방에 속한 유저 수
				String sql = "SELECT * FROM Belonged_Meeting WHERE room_idx = " + room_idx + "";
				int un_read_count = dbConnector.selectNumRows(sql);

				// 메시지 저장
				sql = "INSERT INTO Chatroom_" + room_idx
						+ "_Message (`client_msg_idx`, `sender_idx`, `sender_name`, `room_idx`, `content`, `client_sendtime`, `un_read_count`) VALUES ( "
						+ client_msg_idx + ", " + sender_idx + ", '" + sender_name + "', " + room_idx + ", '"
						+ talk_content + "', '" + client_sendtime + "'," + un_read_count + " )";
				dbConnector.update(sql);

			}

			dbConnector.close();

		}

		catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void reConnect(Socket socket, ObjectInputStream input, ObjectOutputStream output) throws IOException {
		// userSocket.socket.shutdownInput();
		// userSocket.socket.shutdownOutput();

		if (userSocket != null) {
			if (userSocket.socket.isConnected()) {
				userSocket.close();
			}
		}

		userSocket = new UserSocket(socket, input, output);
		System.out.println(this.userID + " user reconnect");
	}

	public void setUserName(String name) {
		userName = name;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public String getUserID() {
		return userID;
	}

	public UserSocket getUserSocket() {
		return userSocket;
	}

	public void sendMsgToRoom(String toRoomID, Message2 message) {
		
		Manager manager = Manager.getInstance();
		if (manager.getRoom(toRoomID) != null) {
			manager.getRoom(toRoomID).broadcastMsg(message);
		}

	}

}
