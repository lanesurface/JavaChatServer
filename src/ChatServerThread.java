import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class ChatServerThread extends Thread {
	private Server server = null;
	private Socket socket = null;
	private int ID = -1;
	private DataInputStream streamIn = null;
	private DataOutputStream streamOut = null;
	
	public ChatServerThread(Server server, Socket socket) {
		super();
		this.server = server;
		this.socket = socket;
		ID = socket.getPort();
	}
	public void send(String message) {
		try {
			streamOut.writeUTF(message);
			streamOut.flush();
		} catch (IOException ie) {
			System.err.println(ID + " ERROR sending: " + ie.getMessage());
			server.remove(ID);
			try {
				join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	public int getID() {
		return ID;
	}
	public void run() {
		System.out.println("Server thread " + ID + " running.");
		
		Scanner sc = new Scanner(System.in);
		while (true) {
			try {
				server.handle(ID, streamIn.readUTF());
			} catch (IOException ie) {
				System.err.println(ID + " ERROR reading: " + ie.getMessage());
				server.remove(ID);
				try {
					join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	public void open() throws IOException {
		streamIn = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
		streamOut = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
	}
	public void close() throws IOException {
		if (socket != null) socket.close();
		if (streamIn != null) streamIn.close();
		if (streamOut != null) streamOut.close();
	}
}
