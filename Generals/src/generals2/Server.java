package generals2;

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

	public static void main(String[] args) throws IOException{
		clients = new ArrayList<>();
		pool = Executors.newFixedThreadPool(8);
		ServerSocket listener = new ServerSocket(9090);

		while (true) {
			System.out.println("[SERVER] Checking for new clients...");
			Socket client = listener.accept();
			System.out.println("[SERVER] Connection made.");
			ServerClientHandler clientThread = new ServerClientHandler(client, clients);
			clients.add(clientThread);
			
			pool.execute(clientThread);
		} 
	}	

}