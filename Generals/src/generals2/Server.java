package generals2;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

	
	private static final int PORT = 9090;
	private static ArrayList<ServerClientHandler> clients;
	private static ExecutorService pool;
	private static int[][] map;

	public static void main(String[] args) throws IOException{
		clients = new ArrayList<>();
		pool = Executors.newFixedThreadPool(8);
		ServerSocket listener = new ServerSocket(9090);
		
		// Generate the new map
		Automata automata = new Automata();
		map = automata.getMap();
		
		// Note: right now the automata script only returns a map of 0's and 1's, will need to add some more screening methods to generate better obstacles and player locations

		while (true) {
			System.out.println("[SERVER] Checking for new clients...");
			Socket client = listener.accept();
			System.out.println("[SERVER] Connection made.");
			ServerClientHandler clientThread = new ServerClientHandler(client, clients, map);
			clients.add(clientThread);
			pool.execute(clientThread);
		} 
	}	
}