package generals2;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class ServerClientHandler implements Runnable{

	private Socket client;
	private BufferedReader in;
	ObjectInputStream tileReceiver;
	ObjectOutputStream out;
	private ArrayList<ServerClientHandler> clients;
	private int[][] map;

	public ServerClientHandler(Socket clientSocket, ArrayList<ServerClientHandler> clients, int[][] map) throws IOException {
		this.client = clientSocket;
		this.clients = clients;
		in = new BufferedReader(new InputStreamReader(client.getInputStream()));
		out = new ObjectOutputStream(client.getOutputStream());
		tileReceiver = new ObjectInputStream(client.getInputStream());
		this.map = map;
	}

	@Override
	public void run() {
		try {
			System.out.println("[SERVER] Sending Map Data...");	
			sendMapData(client);
			while (true) {

				// Process all requests from clients here
				System.out.println("[SERVER] Listening for Move Requests...");
				
				Tile t1 = (Tile) tileReceiver.readObject(); // Read origin tile player is moving from
				Tile t2 = (Tile) tileReceiver.readObject(); // Read tile player is trying to move to

				if (t1 != null && t2 != null) {	
					System.out.println("[SERVER] Request received. Processing...");
					t2.setOwnedBy(t1.getOwnedBy());
					pushTileUpdate(t1, t2);
				}
			}
		} catch (IOException e) {
			System.err.println("IO exception in client handler");
			System.err.println(e.getStackTrace());
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}finally {
			try {
				out.close();
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// Make sure every client can now see the updated tiles
	private void pushTileUpdate(Tile tile1, Tile tile2) throws IOException {
		for (ServerClientHandler c : clients) {
			System.out.println("[SERVER] Pushing update to all connected clients...");
			out.writeObject(tile1);
			out.writeObject(tile2);
		}
	}
	
	
	private void sendMapData(Socket s) throws IOException {
		DataOutputStream out = new DataOutputStream(s.getOutputStream());
		out.writeInt(map.length);		// Send the width
		out.writeInt(map[0].length);	// Send the height
		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map[i].length; j++) {
				out.writeInt(map[i][j]);// Send map index
			}
		}
		
	}
}

















