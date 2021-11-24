package stream;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Scanner;

/**
 * MulticastClient is the instance of a client using udp
 * 
 * The MulticastClient is composed of:
 * <ul>
 * <li>A String that contains the group ip</li>
 * <li>An int that contains the port</li>
 * <li>A MulticastSocket that manages the messages</li>
 * <li>A pseudo that identifies the user</li>
 * </ul>
 * 
 * @author Basma Belahcen, Emma Drapeau, Ihssane Youbi
 * 
 */
public class MulticastClient {
	public String groupChat = "230.0.0.0";
	public int port = 3000;
	public MulticastSocket clientSocket;
	public String pseudo;
	
	/**
	 * class constructor.
	 * All attributes are initialised in this constructor.
	 * 
	 * @param pseudo
	 */
	public MulticastClient(String pseudo) {
		this.pseudo=pseudo;
		try {
			clientSocket = new MulticastSocket(3000);
			clientSocket.joinGroup(InetAddress.getByName(groupChat));
		} catch (IOException e) {
			System.err.println("erreur");
		}
	}

	/**
	 * main method accepts a connection, receives a message from client then sends
	 * an echo to the client
	 * 
	 * @throws IOException
	 **/
	public static void main(String[] args) throws IOException {
		
		System.out.println("Enter your pseudo ");
		Scanner scanner = new Scanner(System.in);
		String pseudo = scanner.nextLine();
		
		
		MulticastClient client = new MulticastClient(pseudo);
		client.receiveMessage();
		client.sendMessage();
		

	}

	/**
	 * A Method that sends the message to the clients connected on the port and the special ip address
	 *  
	 **/
	public void sendMessage() throws IOException {

		while (true) {

			Scanner scanner = new Scanner(System.in);
			String message = scanner.nextLine();
			message= "["+pseudo+"] :"+message;

			DatagramPacket datagramPacketsender = new DatagramPacket(message.getBytes(), message.length(),
					InetAddress.getByName(groupChat), port);

			clientSocket.send(datagramPacketsender);

		}

	}

	/**
	 * A Method that creates a thread listens to the messages sent by other user
	 *  
	 **/
	public void receiveMessage(){

		new Thread(new Runnable() {

			@Override
			public void run() {

				
				try {
					byte[] buffer = new byte[1024];

					DatagramPacket datagramPacket = new DatagramPacket(buffer, 1024);
					while (true) {

						clientSocket.receive(datagramPacket);
						String received = new String(datagramPacket.getData(), 0, datagramPacket.getLength());

						System.out.println(received);
					}
					
				} catch (IOException e) {
				}

			}

		}).start();

	}

}
