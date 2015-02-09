
import java.io.*;
import java.net.*;

public class Server implements Runnable {
	private ChatServerThread clients[] = new ChatServerThread[50];
	private ServerSocket server = null;
	private Thread thread;
	private int clientCount = 0;
	
	public Server(int port) {
		try {
			System.out.println("Binding to port: [" + port + "], please wait...");
			server = new ServerSocket(port);
			System.out.println("Server started: [" + server + "]");
			start();
		} catch (IOException ie) {
			System.err.println(ie);
		}
	}
	public void run() {
		while (thread != null) {
			try {
				System.out.println("Waiting for a client...");
				addThread(server.accept());
			} catch (IOException ie) {
				System.err.println("Acceptance error: [" + ie + "]");
			}
		}
	}
	public void start() {
		if (thread == null) {
			thread = new Thread(this);
			thread.start();
		}
	}
	public void stop() {
		if (thread == null) {
			try {
				thread.join();
				thread = null;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	public int findClient(int ID) {
		for (int i = 0; i < clientCount; i++)
			if (clients[i].getID() == ID)
				return i;
		return -1;
	}
	public synchronized void handle(int ID, String input) {
		String[] tmpMes = input.split(": ");
		if (tmpMes.length > 1) {
			if (tmpMes[1].equals(".quit")) {
				clients[findClient(ID)].send("Bye");
				remove(ID);
			} else {
				System.out.println(input);
				for (int i = 0; i < clientCount; i++) {
					clients[i].send(input);
				}
			}
		} else {
			System.out.println(input);
			for (int i = 0; i < clientCount; i++) {
				clients[i].send(input);
			}
		}
	}
	public synchronized void remove(int ID) {
		int pos = findClient(ID);
		if (pos >= 0) {
			ChatServerThread toTerminate = clients[pos];
			System.out.println("Removing client thread " + ID + " at " + pos);
			if (pos < clientCount - 1)
				for (int i = pos + 1; i < clientCount; i++)
					clients[i - 1] = clients[i];
			System.out.println("Removing client thread...");
			clientCount--;
			try {
				toTerminate.close();
			} catch (IOException ie) {
				System.err.println("Error closing thread: [" + ie + "]");
			}
			try {
				toTerminate.stop();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	public void addThread(Socket socket) {
		if (clientCount < clients.length) {
			System.out.println("Client accepted: [" + socket + "]");
			clients[clientCount] = new ChatServerThread(this, socket);
			try {
				clients[clientCount].open();
				clients[clientCount].start();
				clientCount++;
			} catch (IOException ie) {
				System.err.println("Error opening thread: [" + ie + "]");
			}
		} else {
			System.err.println("Client refused: [maximum amount of clients reached(" + clients.length + ")]");
		}
	}
	public static void main(String[] args) {
		Server s = null;
		
		if (args.length != 1) {
			System.out.println("Usage: JIM port.");
		}
		else {
			s = new Server(Integer.parseInt(args[0]));
		}
	}
}
