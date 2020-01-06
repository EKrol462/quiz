package server1clients;

import java.util.Scanner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;


/**
 * Server Client Class
 * Receives Input from user and sends various objects to the Server
 * 
 * @author Eryk Krol st20124378
 * @version 06/01/2020
 */
public class ServerClient implements Runnable {

	// reference variable for client socket
	private Socket 					clientSocket;

	// reference variable to store object IO streams, should be used when working with serialized objects.
	private ObjectOutputStream 		output;
	private ObjectInputStream 		input;

	// boolean variable to store stopclient flag.
	private boolean 				stopClient;

	// reference variable for Thread
	private Thread 					clientReader;

	// variables to store Host IP and port number
	private String 					host;
	private static int 				port;
    private static String           ip;
	
	//Store Client Objects such as client details, player points, input etc
	public static int 						playerAge;
	public String playerName2 = "Client";
	public String playerInput;
	public String playerAnswer;
	public String playerPoints;
	
	//object to determines if game is started
	public String gameStarted = "false";
	//object to determine if player is ready
	public boolean playerReady = false;
	//object to determine if player has given server details	
	static boolean serverDetailsReceived = false;
	
	
	/**
	 * Constructor, initiates a client, and calls for openConnection.
	 * @param host
	 * @param port
	 * @throws IOException
	 */
	public ServerClient(String host, int port) throws IOException {
		this.host = host;
		this.port = port;
		openConnection();
	}
	
	/**
	 * opens a connection to the server
	 * setup Object IO streams for the socket to send data across the connection.
	 * 
	 * @throws IOException
	 */
	public void openConnection() throws IOException {

		// Create the sockets and the data streams
		try {

			this.clientSocket = new Socket(this.host, this.port);
			this.output = new ObjectOutputStream(this.clientSocket.getOutputStream());
			this.input = new ObjectInputStream(this.clientSocket.getInputStream());

		} catch (IOException ex) {
			try {
				closeAll();
			} catch (Exception exc) {
				System.err.println(playerName2 + ":" + "error in opening a connection to: " + this.host + " on port: " + this.port);
			}

			throw ex; // Rethrow the exception.
		}
		
		// creates a Thread for the user instance and starts the thread.
		this.clientReader = new Thread(this);
		this.stopClient = false;
		this.clientReader.start();

	}
	
	
	/**
	 * Handles sending a message to server. In this case, it is a String. 
	 * @param msg
	 * @throws IOException
	 */

	public void sendMessageToServer(String msg) throws IOException {
		if (this.clientSocket == null || this.output == null)
			throw new SocketException("socket does not exist");

		this.output.writeObject(msg);
	}

	/**
	 * Handles Various messages such as questions, if the player has entered the correct answer etc.
	 * @param msg
	 */	
	public void handleMessageFromServer(String msg) {
		display(msg);

	}
	
	/**
	 * Displays Server Message in the user terminal
	 * @param message
	 */
	public void display(String message) {
		System.out.println("> " + message);
	}
	
	
	/**
	 * Close all connections
	 * @throws IOException
	 */
	private void closeAll() throws IOException {
		try {
			// Close the socket
			if (this.clientSocket != null)
				this.clientSocket.close();

			// Close the output stream
			if (this.output != null)
				this.output.close();

			// Close the input stream
			if (this.input != null)
				this.input.close();
			
		} finally {
			// Set the streams and the sockets to NULL no matter what.
			this.output = null;
			this.input = null;
			this.clientSocket = null;
		}
	}
	
	/**
	 * handles user inputs from the terminal. 
	 *  
	 */
	public void runClient() {
	
		//Writes Player points as an object
		try {
			playerPoints = "0";
			this.output.writeObject(playerPoints);
		} catch (IOException e) {
			System.out.println("Error sending player points");
			e.printStackTrace();
		} 
		
		//Writes player Name as an object to the output Stream

		System.out.println("Welcome, Enter your name :");
		try {
			Scanner playerInfo = new Scanner(System.in);
			playerName2 = playerInfo.nextLine();
			this.output.writeObject(playerName2);
		} catch (IOException e) {
			System.out.println("Error sending player name");
			e.printStackTrace();
		} System.out.println("Welcome to The Game! " + playerName2);

		
		//A ready check for the player to start the game
		String readycheck = "";
		String ready = "ready";
		if (!readycheck.equals(ready) ); {
		Scanner playerRdy = new Scanner(System.in);
		System.out.println("Enter ready to start the game.");
		readycheck = playerRdy.nextLine();
		}
		
		if(readycheck.equals("ready")) {
		try {
				gameStarted = "true";
				this.output.writeObject(gameStarted);
			} catch (IOException e1) {
				System.out.println("Error sending game ready");
				e1.printStackTrace();
			}
		
		/*while(gameStarted.equals("true")) { TODO Implement while check correctly so the input is only taken unitll the game is running
		 *
			for (int i = 0; i < 5; i++) { 
		*/	
		//Writes Player Answer as an object
				
		Scanner playerAns = new Scanner(System.in);
		playerInput = playerAns.nextLine();
		
		try {
			playerAnswer = playerInput;
			this.output.writeObject(playerAnswer);
			System.out.println("Sending Answer");
		} catch (IOException e) {
			System.out.println("Error sending player answer");
			e.printStackTrace();
		} System.out.println("Your answer is: " + playerAnswer);
			}
	//	}
		
		
		
		//}
		// Buffer reader to Input and output information to the server
		 
			try {
				BufferedReader fromConsole = new BufferedReader(new InputStreamReader(System.in));
				String message = null;
	
			

			//Handles user input to send messges to the server, allows the user to finish the connection
			while (true) {
				message = fromConsole.readLine();
				handleUserInput(message);
				if(message.equals("over")) {
						
					break; } //breaks connection is user enters "over"
				} 
			
			
			System.out.println(playerName2 + ":" + "stopping client...");
			this.stopClient = true;
			fromConsole.close();
			//closeAll();
		} catch (Exception ex) {
			System.out.println(playerName2 + ":" + "unexpected error while reading from console!");
		}
	 }
	

	/**
	 * Checks the user input for any errors before sending
	 * 
	 * @param userResponse
	 */
	public void handleUserInput(String userResponse) {

		if (!this.stopClient) {
			try {
				sendMessageToServer(userResponse);
			} catch (IOException e) {
				System.err.println(playerName2 + ":" + "error when sending message to server: " + e.toString());

				try {
					closeAll();
				} catch (IOException ex) {
					System.err.println(playerName2 + ":" + "error closing the client connections: " + ex.toString());
				}
			}
		}
	}
	
	
 /* Allows user to enter server connection details before connecting
  * 
  */
	public static void serverConnectDetails () {
		Scanner serverInfo = new Scanner(System.in);
		
		System.out.println("Enter Server IP:");	
		ip = serverInfo.nextLine();
		System.out.println("Enter Server Port:");
		port = serverInfo.nextInt();
		serverDetailsReceived = true;
		}
	
	/**
	 * The thread that communicates with the server. 
	 * receives a message from the server, passes it to handleMessageFromServer(). 
	 */
	@Override
	public void run() {

		String msg;
		String pName;
		
		// Loop waiting for data

		try {
			while (!this.stopClient) {
				// Reeives data from Server and send it to the handler
				
				msg = (String) input.readObject();

				// Concrete subclasses implements the method
				handleMessageFromServer(msg);
			}
			
			//Closes the connecctions if an error occurs
			System.out.println(playerName2 + ":" + "client stopped..");
		} catch (Exception exception) {
			if (!this.stopClient) {
				try {
					closeAll();
				} catch (Exception ex) {
					System.err.println("[client: ] error in closing the client connection...");
				}
			}
		} finally {
			clientReader = null;
		}
		
		System.out.println(playerName2 + ":" +  "exiting thread...");
	}
	
	/**
	 * Main() to initiate the client.
	 * @param args
	 */
	public static void main(String[] args) {
	
			serverConnectDetails (); //calls the method to gather connection details
			/* establishes connection if the details are received
			 * 
			 */
			if (serverDetailsReceived = true) {							
	

		ServerClient chatclient = null;
		
		// thread to communicate with the server starts here.
		try {
			chatclient = new ServerClient(ip, port);
		} catch (IOException e) {
			System.err.println(":" + "Error in openning the client connection to " + ip + " on port: " + port);
		}
		
		System.out.println("Connected Succesfully, Welcome to The Game!"); //Message when the user connects to the server
		

		// Main thread continues sending messages to the server
		chatclient.runClient();
		}
	}

}
//}