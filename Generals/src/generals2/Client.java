package generals2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;



public class Client {
	private static final String IP = "127.0.0.1";
	private static final int PORT = 9090;
	private static ClientConnectionHandler serverConn;
	private static String playerID = "0";
	private static Tile t1 = new Tile(0, 0, playerID);
	private static Tile t2 = new Tile(0, 1, "1");


	public static void main(String[] args) throws UnknownHostException, IOException {

		// Connect to the server, and read the information from the server
		Socket socket = new Socket(IP, PORT);

		serverConn = new ClientConnectionHandler(socket);		

		//ClientSide sending data to server
		PrintWriter sendID = new PrintWriter(socket.getOutputStream(), true); 
		ObjectOutputStream tileSender = new ObjectOutputStream(socket.getOutputStream());


		// Do not want a pool of threads, since there is only one connection happening
		new Thread(serverConn).start();

		// Client sends move requests
		while (true) {

			//Simulate waiting for player movement updates
			BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
			String command = keyboard.readLine();
			if (command.equals("quit")) { break; }

			System.out.println("[CLIENT] Sending player move request...");
			tileSender.writeObject(t1); // Tile of origin
			tileSender.writeObject(t2); // Tile 2 move 2
		}

		socket.close();

	}
}
