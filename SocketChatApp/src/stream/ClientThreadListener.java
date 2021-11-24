package stream;

import java.io.*;

/**
 * ClientThreadListener is the client Thread listening and waiting for the reception of messages from other clients
 * 
 * A clientThreadListener is composed of:
 * <ul>
 * <li>A BufferedReader socIn where the messages are received</li>
 * <li>An EchoClient client the client corresponding to the listener</li>
 * <li>A MainPage main corresponding to the GUI window</li>
 * </ul>
 * 
 * A clienThreadListener is created for each client connected to the App
 * When a client connects to the App this thread allows them to receive messages.<br/>
 * 
 * ClientThreadListener extends from Thread
 * 
 * @author Basma BELAHCEN, Ihssane YOUBI et Emma DRAPEAU
 * 
 * @see Thread
 * @see EchoClient
 * @see MainPage
 *
 */
public class ClientThreadListener extends Thread {

	private BufferedReader socIn;
	private EchoClient client;
	public MainPage main;

	/**
	 * Default class constructor.
	 * 
	 */
	ClientThreadListener(BufferedReader SocIn,EchoClient client, MainPage main) {
		this.main = main;
		this.socIn = SocIn;
		this.client =client;
	}

	/**
	 * Run function receives incoming messages from the input Socket
	 * 
	 */
	public void run() {
		String incomingMessage;
		while(true) {
			try {
				incomingMessage = socIn.readLine();
				main.messageLog.setText(main.messageLog.getText()+ "\n"+incomingMessage);
			} catch (IOException e) {
				try {
					if(socIn != null) {
						socIn.close();
					}
				} catch (IOException exp) {
					exp.printStackTrace();
				}
			}
		}
	}

}
