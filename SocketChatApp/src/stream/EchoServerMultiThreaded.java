package stream;

import java.io.IOException;
import java.net.*;

/**
 * EchoServerMultiThreaded is created when the application is launched.
 * This Server handles as much clients as needed.
 * This Server allows them to communicate between them
 * 
 * A clientThreadListener is composed of:
 * <ul>
 * <li>A ServerSocket </li>
 * </ul>
 * 
 * 
 * @author Basma BELAHCEN, Ihssane YOUBI et Emma DRAPEAU
 * 
 * @see java.net.ServerSocket
 *
 */

public class EchoServerMultiThreaded {

	private ServerSocket serverSocket;
	
	/**
	 * Default class constructor.
	 * 
	 */
	public EchoServerMultiThreaded(ServerSocket serverSocket) {
		this.serverSocket = serverSocket;
	}
	
	/**
	 * Accepts new client connections
	 * creates and runs clientThread for each client connected
	 * 
	 */
	public void startServer() {
		
		try {
			while(true) {
				
				Socket socket = serverSocket.accept();
				System.out.println("A new Client has connected!");
				ClientThread clientThread = new ClientThread(socket);
				clientThread.start();
				
			}
		}catch (IOException e) {
			closeServerSocket();
		}
	}
	/**
	 * Closes the serverSocket
	 * 
	 */	
	public void closeServerSocket() {
		try {
			if(serverSocket != null) {
				serverSocket.close();
			}
		} catch ( IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * main method
	 * 
	 * @param args not used
	 * 
	 **/
	public static void main(String args[]) {
		ServerSocket listenSocket;

		try {
			listenSocket = new ServerSocket(49998); // port
			EchoServerMultiThreaded server = new EchoServerMultiThreaded(listenSocket);
			server.startServer();
		} catch (Exception e) {
			System.err.println("Error in EchoServer:" + e);
		}
	}
}
