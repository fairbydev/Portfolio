package kr.teamwd.woori.Chat.Server.File;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class FileSender extends Thread {

	Socket dataSocket;
	
	public FileSender(Socket socket) {
		this.dataSocket = socket;
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

			String fileNameStr = dis.readUTF(); // 클라이언트로부터 파일명얻기
			System.out.println("사용자가 요청한 파일 :" + fileNameStr);

			String dirNameStr = "/var/www/html/Woori/File/";
			// 서버프로그램이 실행되는 컴퓨터에 파일폴더로 사용할 폴더 생성.
			FileInputStream fin = new FileInputStream(dirNameStr + fileNameStr);

			while (true) { // FileInputStream을 통해 파일을 읽어들여서 소켓의 출력스트림을 통해 출력.
				int data = fin.read();
				if (data == -1)
					break;
				dos.write(data);
			}

			// 스트림 , 소켓 닫기
			fin.close();
			dos.close();
			dis.close();
			dataSocket.close();

		}

		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
	}
}
