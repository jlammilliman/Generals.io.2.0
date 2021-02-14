package generals2;

import java.io.BufferedReader;
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

	public ServerClientHandler(Socket clientSocket, ArrayList<ServerClientHandler> clients) throws IOException {
		this.client = clientSocket;
		this.clients = clients;
		in = new BufferedReader(new InputStreamReader(client.getInputStream()));
		out = new ObjectOutputStream(client.getOutputStream());
		tileReceiver = new ObjectInputStream(client.getInputStream());
		
	}

	@Override
	public void run() {
		try {
			while (true) {
				
				// Process all requests from clients here
				System.out.println("[SERVER] Receving Move Requests...");
				String playerID = in.readLine(); // Grab whatever number the player is
				Tile t1 = (Tile) tileReceiver.readObject(); // Read origin tile player is moving from
				Tile t2 = (Tile) tileReceiver.readObject(); // Read tile player is trying to move to
				
				System.out.println("Tile 1 is owned by: " + t1.getOwnedBy());
				System.out.println("Tile 2 is owned by: " + t2.getOwnedBy());
				
				System.out.println("[SERVER] Processing request...");
				t2.setOwnedBy(playerID);
				pushTileUpdate(t1, t2);
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
			out.writeObject(tile1);
			out.writeObject(tile2);
		}
	}
}
