package kr.teamwd.woori.Chat.Server.File;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class FileReceiver extends Thread{
	
	Socket dataSocket;
	
	public FileReceiver(Socket socket) 
	{
		dataSocket = socket;
		this.start();
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
		try {
			
			InputStream in = dataSocket.getInputStream();
			DataInputStream dis = new DataInputStream(in);

			OutputStream out = dataSocket.getOutputStream();
			DataOutputStream dos = new DataOutputStream(out);

			String fileName = dis.readUTF(); // 클라이언트로부터 파일명얻기
			System.out.println("사용자가 업로드중인 파일 :" + fileName);

			String dirName = "/var/www/html/Woori/File/";
			// 서버프로그램이 실행되는 컴퓨터에 파일폴더로 사용할 폴더 생성.
			
			FileOutputStream fos = new FileOutputStream(dirName+fileName);
			
			while(true){
	            int data=dis.read(/*buffer*/);
	            if(data == -1) break;
	            fos.write(data);
	        }

			System.out.println(fileName + " 파일 업로드 완료");
			
			// 스트림 , 소켓 닫기
			fos.close();
			dos.close();
			dis.close();
			dataSocket.close();
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
	}

}
