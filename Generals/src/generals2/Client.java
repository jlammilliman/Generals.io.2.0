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

		// Send mock tile move request to server, see if we get a response
		while (true) {
			
			//Simulate waiting for player movement updates
			BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
			String command = keyboard.readLine();
			if (command.equals("quit")) { break; }
			
			System.out.println("> Sending player move request...");
			sendID.println(playerID);
			tileSender.writeObject(t1);
			tileSender.writeObject(t2);

			System.out.println("Tile 1 is owned by: " + t1.getOwnedBy());
			System.out.println("Tile 2 is owned by: " + t2.getOwnedBy());
		}

		socket.close();

		// For testing purposes


	}
}
