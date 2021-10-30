import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

class Reader_Thread extends Thread{
	private Screen serverScreen;
	private DataInputStream in;
	private DataOutputStream out;

	public Reader_Thread(Screen serverScreen, Socket clientSocket){
        try {
        	this.serverScreen = serverScreen;        	
			this.in = new DataInputStream(clientSocket.getInputStream());
			this.out = new DataOutputStream(clientSocket.getOutputStream());
		} catch (Exception e) {
			e.printStackTrace();	
		}
    }

	public void run() {
		while (true) {
			try {
				String data = in.readUTF();
				System.out.println("Reader Thread > Data recieved: " + data);
				serverScreen.handleMessage(data, out);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}
}
