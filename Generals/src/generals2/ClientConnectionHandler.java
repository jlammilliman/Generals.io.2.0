package generals2;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.net.Socket;

public class ClientConnectionHandler implements Runnable {

	private Socket server;
	private BufferedReader in;
	private ObjectInputStream tileIn;
	private int[][] map;



	public ClientConnectionHandler(Socket s) throws IOException {
		this.server = s;
		tileIn = new ObjectInputStream(server.getInputStream());
	}


	// Keep trying to receive information from the server and print it out
	@Override
	public void run() {
		try {
			readMap(server);
			while (true) {
				// Receive all tile updates here
				Tile t1 = (Tile) tileIn.readObject();
				Tile t2 = (Tile) tileIn.readObject();

				System.out.println("[CLIENT] Received tile updates, updating map...");
				// Update map here
				System.out.println("Tile 1 is owned by: " + t1.getOwnedBy());
				System.out.println("Tile 2 is owned by: " + t2.getOwnedBy());
			}

		} catch (IOException e) {
			System.err.println("!!! Woops Bad happened, IOException in connection handler !!!");
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			System.err.println("!!! Did not get a tile in ClientConnectionHandler !!!");
			e.printStackTrace();
		} finally {
			try {
				tileIn.close();
			} catch (IOException e) {
				System.err.println("!!! Woops Bad happened trying to close tileIn!!!");
				e.printStackTrace();
			}

		}
	}


	private void readMap(Socket s) throws IOException {
		DataInputStream in = new DataInputStream(s.getInputStream());
		int width = in.readInt();	// Read the width and height from the server
		int height = in.readInt();	
		this.map = new int[width][height];
		for(int i = 0; i < width; i++) {
			for(int j = 0; j < height; j++) {
				map[i][j] = in.readInt();	// Get map tile type and save locally in an int[][] map
			}
		}

	}
	
	public int[][] getMap(){
		return map;
	}
}
