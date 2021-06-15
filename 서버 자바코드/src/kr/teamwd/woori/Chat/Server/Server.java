package kr.teamwd.woori.Chat.Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import kr.teamwd.woori.Chat.Message2;
import kr.teamwd.woori.Chat.Server.File.FileReceiver;
import kr.teamwd.woori.Chat.Server.File.FileSender;

public class Server {
	
	static final int PORT = 9999;

	public static void main(String[] args) throws IOException 
	{
		
		//파일 전송 쓰레드(서버 -> 클라이언트)
		Thread senderThread = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				int port = 10000;
				ServerSocket senderListenSocket = null; 

				try {
					// ServerSocket 생성.
					senderListenSocket = new ServerSocket(port);
					System.out.println("(파일 송신)접속 대기중...");

					while (true) {
						Socket senderSocket = senderListenSocket.accept(); // 클라이언트접속
																			// 대기
						System.out.println("파일 송신 소켓 준비 완료");
						new FileSender(senderSocket);
					}

				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
				
				finally{
					if(senderListenSocket != null)
					{
						try {
							senderListenSocket.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		});

		senderThread.start();

		//파일 수신 쓰레드 (클라이언트 -> 서버)
		Thread receiveThread = new Thread(new Runnable() {
			public void run() {
				int port = 10001;

				ServerSocket receiveListenSocket = null;

				try {

					receiveListenSocket = new ServerSocket(port);
					System.out.println("(파일 수신)접속 대기중...");

					while (true) {
						Socket receiverSocket = receiveListenSocket.accept(); // 클라이언트접속
																				// 대기
						System.out.println("파일 소켓 준비 완료");
						new FileReceiver(receiverSocket);
					}

				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}

				finally{
					if (receiveListenSocket != null) {
						try {
							receiveListenSocket.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		});

		receiveThread.start();
		
		Manager manager = Manager.getInstance();	
		ServerSocket userListenSocket = new ServerSocket(PORT);				
		
		while(true)
		{
			System.out.println("Waiting Connecting...");
			Socket userSocket = userListenSocket.accept();	
			//userSocket.setSoTimeout(30000);
			
			if(userSocket != null)
			{
				
				System.out.println("Connection(Client) Information : "+userSocket.getInetAddress()+" / "+userSocket.getLocalPort());												
								
					Thread connectionThread = new Thread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							//클라이언트로부터 데이터 수신
							try {
								userSocket.setSoTimeout(30000);
								ObjectInputStream input = new ObjectInputStream(userSocket.getInputStream());
								ObjectOutputStream output = new ObjectOutputStream(userSocket.getOutputStream());
								
								Message2 msg;
								msg = (Message2) input.readObject();
								
								if(msg != null)
				                {	     
									int systemType = msg.getSystemType();
									
									switch(systemType)
									{
										case Message2.SystemMessage.ALIVE:
											
											String userID = msg.getSender_idx();
							                String userName = msg.getSender_name();
											User user = manager.findUser(userID);
					        				
					        	    		//신규 유저         		
					        	    		if(user == null)
					        	    		{
					        	    			user = new User(userName, userID, userSocket, input, output);        			
					        	    			manager.addUser(user);
					        	    		}
					        	    		
					        	    		//기존 유저
					        	    		else
					        	    		{	        	    			
					        	    			user.reConnect(userSocket, input, output);        			
					        	    		}
											break;
									}
				                }
								
							} catch (SocketException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (ClassNotFoundException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}																									
							
						}
														
					});
					connectionThread.start();
					
			}						
		}	
	}	
}
