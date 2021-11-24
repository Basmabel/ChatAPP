package stream;

import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

/**
 * CLientThread is the thread that allows the sending of the messages
 * 
 * The CLientThread is composed of:
 * <ul>
 * <li>A Socket that contains the message.</li>
 * <li>A BufferedReader that reads the message inside the socket.</li>
 * <li>A PrintStream that writes the message inside the socket and sends it to the other user</li>
 * <li>A pseudo that identifies the user</li>
 * <li>A static list of clientThreads to identify all the clientThreads that are logged in</li>
 * <li>A diffusion list of clientThreads to identify all the clientThreads that we are sending messages to</li>
 * <li>A boolean to know if we are in the general conversation</li>
 * <li>A boolean to know if the user was chosen by another user to conduct a conversation</li>
 * <li>A file that allows to store the history of the general chat</li>
 * <li>A printwriter to allow the writing inside a file<li>
 * </ul>
 * 
 * @author Basma BELAHCEN, Ihssane YOUBI et Emma DRAPEAU
 * 
 * @see stream.EchoClient
 */
public class ClientThread extends Thread {

	private Socket clientSocket;
	public BufferedReader socIn;
	public PrintStream socOut;
	public String pseudo;
	public static ArrayList<ClientThread> listClients = new ArrayList<>();
	public ArrayList<ClientThread> diffusionList = new ArrayList<>();
	public Boolean general = false;
	public Boolean choose = false;
	public File file = new File("historique1.txt");
	public static PrintWriter out;

	/**
	 * class constructor.
	 * All attributes are initialized in this constructor.
	 * 
	 * @param s the Socket that contains the information
	 */
	public ClientThread(Socket s) {
		try {
			this.clientSocket = s;
			this.socIn = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
			this.socOut = new PrintStream(clientSocket.getOutputStream());
			this.pseudo = socIn.readLine();

			if (file.length() == 0) {
				out = new PrintWriter("historique.txt");
			} else {
				out = new PrintWriter(new FileOutputStream(file, true));
			}

			listClients.add(this);
			sendMessageGeneral("[SERVER]" + this.pseudo + " has entered the chat!");

		} catch (Exception e) {
			closeEverything(clientSocket, socIn, socOut);
		}
	}

	/**
	 * receives a request from client then sends an echo to the client
	 **/
	public void run() {
		try {

			if (file.length() != 0) {
				socOut.println("[MESSAGE HISTORY OF GENERAL CONVERSATION]");
				socOut.println(Files.readString(Path.of("historique1.txt")));
				socOut.println("[END MESSAGE HISTORIQUE]");

			}

			conversation();
		} catch (Exception e) {
			closeEverything(clientSocket, socIn, socOut);
		}
	}

	/**
	 * A Method that allows to run the general chat or the group
	 * 
	 **/
	public void conversation() throws IOException {

		this.choose=false;
		for (ClientThread clientThread: diffusionList) {
			clientThread.diffusionList.remove(this);
		}
		diffusionList = new ArrayList<>();
		if (!choose) {
			socOut.println(
					"[SERVER] to enter a General conversation type \"general\" and to enter a group conversation type \"group\"");
			if (!choose) {
				String conv = socIn.readLine();
				conv = conv.substring(this.pseudo.length() + 3);
				if (conv.equals("general")) {
					if (file.length() != 0) {
						socOut.println("[MESSAGE HISTORY OF GENERAL CONVERSATION]");
						socOut.println(Files.readString(Path.of("historique1.txt")));
						socOut.println("[END MESSAGE HISTORIQUE]");

					}
					general = true;
					socOut.println("[SERVER] You can now Type your message");
					while (true) {
						String line = socIn.readLine();
						if(line.contains("Return")) {
							sendMessageGeneral("["+pseudo+"] Has left the chat");
							conversation();
							break;
							
						}
						sendMessageGeneral(line);
						out = new PrintWriter(new FileOutputStream(file, true));
						out.println(line);
						out.close();
					}
				} else if (conv.contentEquals("group")) {
					if (!choose) {
						chooseUsers();
					}
					while (true) {
						String line = socIn.readLine();
						if(line.contains("Return")) {
							sendMessage("["+pseudo+"] Has left the chat");
							conversation();
							break;
							
						}
						sendMessage(line);
					}

				} else {
					if (choose) {
						sendMessage("[" + pseudo + "]: " + conv);
						while (true) {
							String line = socIn.readLine();
							sendMessage(line);
						}
					} else {
						socOut.println("Please Enter a valid choice!");
						conversation();
					}
				}
			}
		}
		conversation();
		

	}

	/**
	 * A Method that allows to choose the users we want to communicate with in a group.
	 * 
	 **/
	public void chooseUsers() {

		try {
			sendListConnected();
			if (!choose) {
				socOut.println("[SERVER] Type the user(s) you want to communicate with( Enter the first one first)");
				String users = socIn.readLine();
				if (!choose) {
					users = users.substring(this.pseudo.length() + 3);
					if (getClientThread(users) != null) {
						if (getClientThread(users) != this) {
							if (!choose) {
								if (getClientThread(users).diffusionList.isEmpty() && !getClientThread(users).general) {
									getClientThread(users).choose = true;
									getClientThread(users).socOut
											.println("[SERVER] You have been added to a conversation by " + pseudo);
									getClientThread(users).diffusionList.add(this);
									getClientThread(users).socOut
											.println("[" + pseudo + "] Was added to your diffusionList");
									diffusionList.add(getClientThread(users));
									socOut.println(
											"The User: " + users + " was successfully added to your conversation");
									socOut.println(
											"Do you want to add another user? (Answer with Y for Yes or N for no)");
									String choice = socIn.readLine().substring(this.pseudo.length() + 3);
									if (choice.equals("Y")) {
										chooseUsers();
									}
									for (ClientThread client : diffusionList) {
										for (ClientThread clientAdded : diffusionList) {
											if (!clientAdded.pseudo.equals(client.pseudo)) {
												client.diffusionList.add(clientAdded);
												client.socOut.println(
														"[" + clientAdded.pseudo + "] Was added to your diffusionList");
											}

										}
									}

								} else {
									socOut.println(
											"The User you choose is in another conversation. Please choose another one.");
									chooseUsers();
								}
							}
						}
					} else {
						socOut.println("Please enter a User that is not Yourself");
					}
				} else {
					socOut.println(users + " isn't a valid pseudo, please enter a valid user's pseudo");
					chooseUsers();
				}
			} else {
				socOut.println("[SERVER] You are now in a conversation send the message that you want");
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * A Method that gets the clientThread by his pseudo by searching in the static list
	 * 
	 * @param pseudo the pseudo of the user we want to search for 
	 **/
	public ClientThread getClientThread(String pseudo) {
		ClientThread client = null;

		for (ClientThread c : listClients) {
			if (c.pseudo.equals(pseudo)) {
				client = c;
			}
		}
		return client;
	}

	/**
	 * A Method that sends the message to the specific clients in the diffusion list
	 *  
	 * @param message the message we want to send
	 **/
	public void sendMessage(String message) throws IOException {
		for (ClientThread client : diffusionList) {
			client.socOut.println(message);
			client.socOut.flush();
		}
	}

	/**
	 * A Method that sends the message to the general conversation
	 *  
	 * @param message the message we want to send
	 **/
	public void sendMessageGeneral(String message) throws IOException {
		for (ClientThread client : listClients) {
			if (!client.pseudo.equals(pseudo)) {
				if (client.general) {
					client.socOut.println(message);
					client.socOut.flush();
				}
			}
		}
	}

	/**
	 * A Method that returns the list of connected users
	 *  
	 **/
	public void sendListConnected() throws IOException {
		socOut.println("Connected Users :");
		int index = 1;
		for (ClientThread client : listClients) {
			if (!client.pseudo.contentEquals(pseudo)) {
				socOut.println("User " + index + ": " + client.pseudo);
				index++;
			}
		}
	}

	/**
	 * A Method that removes a client from the list of general clients when he deconnects
	 *  
	 **/
	public void removeClient() throws IOException {
		listClients.remove(this);
		sendMessage(" L'utilisateur :" + this.pseudo + " a quitté le chat!");
	}

	/**
	 * A Method that Closes everything we opened
	 * 
	 * @param socket The socket
	 * @param reader The BufferedReader
	 * @param socOut2 The PrintStream
	 *  
	 **/
	public void closeEverything(Socket socket, BufferedReader reader, PrintStream socOut2) {
		try {
			removeClient();

			if (reader != null) {
				reader.close();
			}

			if (socOut2 != null) {
				socOut2.close();
			}

			if (socket != null) {
				socket.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
