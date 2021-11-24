package stream;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

/**
 * EchoClient is the instance of a client
 * 
 * The CLientThread is composed of:
 * <ul>
 * <li>A Socket that contains the message.</li>
 * <li>A BufferedReader that reads the message inside the socket.</li>
 * <li>A PrintStream that writes the message inside the socket and sends it to the other user</li>
 * <li>A pseudo that identifies the user</li>
 * <li>A diffusion list of clientThreads to identify all the clientThreads that we are sending messages to</li>
 * <li>A MainPage that represents the view of the application</li>
 * <li>A booleanThat is unused</li>
 * </ul>
 * 
 * @author Basma BELAHCEN, Ihssane YOUBI et Emma DRAPEAU
 * 
 * @see stream.EchoClient
 */
public class EchoClient {

	private Socket clientSocket;
	public BufferedReader socIn;
	public PrintStream socOut;
	public String pseudo;
	public ArrayList<EchoClient> diffusionList = new ArrayList<>();
	public MainPage main;
	public static Boolean unused = false;

	/**
	 * class constructor.
	 * All attributes are initialised in this constructor.
	 * 
	 * @param s the Socket that contains the information
	 * @param pseudo the pseudo
	 * @param main the MainPage
	 */
	public EchoClient(Socket s, String pseudo, MainPage main) {

		try {
			this.clientSocket = s;
			this.socIn = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
			this.socOut = new PrintStream(clientSocket.getOutputStream());
			this.pseudo = pseudo;
			this.main = main;

		} catch (IOException e) {
			try {
				if (socIn != null) {
					socIn.close();
				}

				if (socOut != null) {
					socOut.close();
				}

				if (clientSocket != null) {
					clientSocket.close();
				}
			} catch (IOException exception) {
				System.err.println(exception);
			}
		}
	}

	/**
	 * main method accepts a connection, receives a message from client then sends
	 * an echo to the client
	 * 
	 * @throws IOException
	 **/
	public static void main(String[] args) throws IOException {

		MainPage main = new MainPage();
		String log = "[SERVER] Entrer votre Pseudo: \n";
		main.messageLog.setText(log);
		
		while (true) {

			if (main.clicked) {
				String pseudo = main.message.getText();
				main.clicked = false;
				main.message.setText("");
				Socket echoSocket = new Socket("localhost", 49998);
				EchoClient client = new EchoClient(echoSocket, pseudo, main);
				client.messageListener();
				client.sendMessage();
				break;
			} else {
				unused = true;
			}

		}

	}

	/**
	 * A Method that sends the message to the server
	 *  
	 **/
	public void sendMessage() {
		socOut.println(pseudo);
		socOut.flush();

		while (true) {
			if (main.clicked) {
				String lines = main.message.getText();
				main.message.setText("");
				main.clicked = false;
				socOut.println("[" + this.pseudo + "] " + lines);
				socOut.flush();
			} else {
				unused = true;
			}

		}
	}

	/**
	 * A Method that listens to the messages sent by other user
	 *  
	 **/
	public void messageListener() {

		ClientThreadListener clientThreadlistener = new ClientThreadListener(socIn, this, main);
		clientThreadlistener.start();
	}

}
