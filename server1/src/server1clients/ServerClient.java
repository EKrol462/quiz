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
 * Represents a client. 
 * Allows user inputs through keyboard and pass them to the server. 
 * Receives responses from the user. 
 * 
 * @author thanuja
 * @version 20.11.2019
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
	


	//String pName = playerName;
	
	//Store Client Age
	public static int 						playerAge;
	
	public String playerName2 = "Client";
	
	public String[] playerInput = new String[4];
	
	public String playerAnswer [] = new String[4]; // player answer to question
	
	public String playerPoints;
	
	String gameQuestion;

	public String gameStarted = "false";
	
	public boolean playerReady = false;
	
	
	static boolean playerDetailsReceived = false;
	
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
	 * setup Object IO streams for the socket.
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
		
		// creates a Thread instance and starts the thread.
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
	 * Handle message from the server. In this case, simply display them. 
	 * @param msg
	 */
	

	public void sendQuestionToServer(String gQuestion) {
		//gameQuestion = gQuestion;
		display(gQuestion);
	} 
	
	
	public void handleMessageFromServer(String msg) {
		display(msg);

	}
	
	/**
	 * Simply display a String message in the terminal. 
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
	 * This should run as a separate thread. In this case, main thread. 
	 * 
	 */
	public void runClient() {
	
		//Writes Player points as an object
		try {
			playerPoints = "60";
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
		//System.out.println(gameQuestion);
		
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
		
		if(gameStarted.equals("true")) {
	
			for (int i = 0; i < 5; i++) {
		//Writes Player Answer as an object
		Scanner playerAns = new Scanner(System.in);
		playerInput[i] = playerAns.nextLine();
		
		try {
			playerAnswer[i] = playerInput[i];
			this.output.writeObject(playerAnswer[i]);
		} catch (IOException e) {
			System.out.println("Error sending player answer");
			e.printStackTrace();
		} System.out.println("Your answer is: " + playerAnswer[i]);
		//gameStarted = "false";
		} 
		}
		//}
		/*
			//Answer 2
			try {
				Scanner playerAns = new Scanner(System.in);
				playerAnswer = playerAns.nextLine();
				this.output.writeObject(playerAnswer);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} System.out.println("Your answer is: " + playerAnswer);
			//} 
			//} */
		
		
			try {
				BufferedReader fromConsole = new BufferedReader(new InputStreamReader(System.in));
				String message = null;
	
			

				
			while (true) {
				message = fromConsole.readLine();
				handleUserInput(message);
			/*	if(message.equals("ready")) {
					System.out.println("You are ready!");
					playerReady = true;
				} */
				if(message.equals("over")) {
						
					break; }
				} 
			
			System.out.println(playerName2 + ":" + "stopping client...");
			this.stopClient = true;
			fromConsole.close();
			//closeAll();
		} catch (Exception ex) {
			System.out.println(playerName2 + ":" + "unexpected error while reading from console!");
		}
	 }// end ready check
	}

	/**
	 * Can perform any pre-processing or checking of the user input before sending it to server. 
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
	
	/**
	 * The thread that communicates with the server. 
	 * receives a message from the server, passes it to handleMessageFromServer(). 
	 * 
	 */
	/*
	public static void playerDetails() {
		Scanner playerInfo = new Scanner(System.in);
		
		System.out.println("Welcome! Please Enter Your Player Name:");
		playerName = playerInfo.nextLine();
		System.out.println("What is your age?");
		playerAge = playerInfo.nextInt();
		System.out.println("Welcome " + playerName + " " + playerAge);
		playerDetailsReceived = true;
		
	}  */
	
	
	public static void serverConnectDetails () {
		Scanner serverInfo = new Scanner(System.in);
		
		System.out.println("Enter Server IP:");	
		ip = serverInfo.nextLine();
		System.out.println("Enter Server Port:");
		port = serverInfo.nextInt();
		serverDetailsReceived = true;
		}
	
	@Override
	public void run() {

		String msg;
		String pName;
		String gQuestion;
		
		// Loop waiting for data

		try {
			while (!this.stopClient) {
				// Get data from Server and send it to the handler
				// The thread waits indefinitely at the following
				// statement until something is received from the server
				//gQuestion = (String) input.readObject();
				msg = (String) input.readObject();

				// Concrete subclasses do what they want with the
				// msg by implementing the following method
				//handleQuestionFromServer(gQuestion);
				handleMessageFromServer(msg);
			}
			
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
	
			serverConnectDetails ();
			if (serverDetailsReceived = true) {							
	

		ServerClient chatclient = null;
		
		// thread to communicate with the server starts here.
		try {
			chatclient = new ServerClient(ip, port);
		} catch (IOException e) {
			System.err.println(":" + "Error in openning the client connection to " + ip + " on port: " + port);
		}
		
		System.out.println("Connected Succesfully, Welcome to The Game!");
		

		// Main thread continues and in this case used to handle user inputs from the terminal.
		chatclient.runClient();
		}
	}

}
//}