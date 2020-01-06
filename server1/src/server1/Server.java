package server1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
* @author Eryk Krol st20124378
* @version 06/01/2020
*/


public class Server extends ServerAbstractComponents implements Runnable {
	
	// reference variable for server socket. 
		private ServerSocket 			serverSocket;

		// reference variable for ClientHandler for the server. 
		private ServerClientManager 			clientHandler;

		// reference variable to store object IO streams, should be used when working with serialized objects.
		private ObjectOutputStream 		output;
		
		// boolean flag to indicate the server stop. 
		private boolean 				stopServer;

		// reference variable for the Thread
		private Thread 					serverListenerThread;

		// reference variable for ThreadGroup when handling multiple clients
		private ThreadGroup 			clientThreadGroup;

		// variable to store server's port number
		int port;
		
		
		public Server() {
			
			
			/**
			 * Initializes the ThreadGroup to allow more client connections and objects
			 */
			this.clientThreadGroup = new ThreadGroup("ClientManager threads");
			
		}
		
		/**
		 * Initializes the server. Takes port number, creates a new server socket instance. 
		 * Starts the server's listening thread. 
		 * Currently the server starts at local host (127.0.0.1) and port 7777
		 * @param port
		 * @throws IOException
		 */
		public void initializeServer(int port) throws IOException {

			this.port = port;
			if (serverSocket == null) {
				serverSocket = new ServerSocket(port);
			}

			stopServer = false;
			serverListenerThread = new Thread(this);
			serverListenerThread.start();


		}
  
		/* All Objects coming from the output stream are below
		 * 
		 * @param client
		 * */
		
		//Method for inputing player Name
		public String playerName2;
		public synchronized void sendNameToServer2(String pName2, ServerClientManager client) {
			client.playerName2 = pName2; //creates local variable of the output, assigned to the unique client
		
		  	}
		//Method for inputing player points
		public int playerPoints;
		public synchronized void sendPointsToServer(String pPoints, ServerClientManager client) {
			client.playerPoints = Integer.parseInt(pPoints); //creates local variable of the output, assigned to the unique client, converts from string to integer

		}
		
		//Method for inputing game started state
		public String gameStarted;
		int questionSent = 0 ; //Count of questions the server has sent, allows for all users to answer questions at the same time
		public synchronized void sendPlayerReady(String gStarted, ServerClientManager client) {
			client.gameStarted = gStarted; //creates local variable of the output, assigned to the unique client
			
			ArrayList<String> gameQuestion = new ArrayList<String>(); //Array list for the game questions, more questions are stored in the method below
			gameQuestion.add("What is The Planet Closest to The Sun? A.MERCURY  B. SATURN  C.VENUS");	
			
			//Displays the first Question when player enters the "ready state"
			if (client.gameStarted.equals("true") )
			{
			if(questionSent == 0) { 
			sendMessageToClient(gameQuestion.get(0), client); //sends the question to client			
			}
			}
			
		}
			
				
		
		public String playerAnswer;
		public String [] gameAnswer = {"A", "B", "A", "C", "A",}; //Array for correct answers
		public synchronized void sendAnswerToServer(String ans, ServerClientManager client) {
			client.playerAnswer = ans; //Player answer, object from output stream, saved as a local variable

			//Array list for user questions
			
			ArrayList<String> gameQuestion = new ArrayList<String>();
			gameQuestion.add("When Microsoft Established?  A.1968  B.1975  C.1980");
			gameQuestion.add("How Many NBA Championships did Michael Jordan Win With The Chicago Bulls? A. 6 B. 2 C. 8");	
			gameQuestion.add("Who Holds The Current 100M Sprint World Record? A. Michael Phelps B.Wayne Gretzky C. Usain Bolt");
			gameQuestion.add(" What temperature does water boil at? A. 100c B.80c C.120c");
			
			/**
			 * TODO
			 * This Method is work in progress, allows user to loop through all questions, the loop will be adjusted
			 * depending how many questions the game has after XML script implementation
			 * 
			 	if (client.gameStarted.equals("true") )
			{
			for (int i = 0; i < 5; i++) {
				 client.playerAnswer = ans;
				 String response;
				 sendMessageToClient(gameQuestion.get(i) client);
				 System.out.println("Game Answer:"+ gameAnswer[i]);
				 System.out.println("player Answer:" + client.playerAnswer);
			     
			 if(client.playerAnswer.equals(gameAnswer[i])) {
		        //prepare a response for the client. 
				response = "[Server:]: That's correct!";				
				} else {
					response = "[Server:]: That's incorrect!";
					 }
				sendMessageToClient(response, client);
				
				client.playerAnswer = "";
		} 
			}
			client.gameStarted = "false"; //Shuts off the game after all questions */
			
			/**
			 * Method to check the player question answer against the correct answer from the server 
			 */
			
			if (client.gameStarted.equals("true") && questionSent == 0 ) //makes sure that the game state is on and that the correct question is being checked
			{
			String response; //Message which the server will send
			//Outputs to the server what the user has entered and the correct answer, will allow for moderating the game
			System.out.println("Game Answer:"+ gameAnswer[0]); 
			System.out.println("player Answer:" + client.playerAnswer);

			
			if(client.playerAnswer.equals(gameAnswer[0])) //This will be called if the correct answer is entered
			{
	        //prepare a response for the client. 
			response = "[Server:]: That's correct!";
			client.playerPoints++; //adds player points
			} else {
				response = "[Server:]: That's incorrect!";  //This will be called if the incorrect answer is entered
				 }
			sendMessageToClient(response, client); //sends relevant response
			client.playerAnswer = null; //resets player answer
			response = null; //resets player response
			questionSent++; //Adds 1 to question sent count (will allow next question to be sent
		/**
		 * TODO correct and implement other questions
		 * Methods for all other questions to be sent, curently disabled due to bugs
		 * 
			client.playerAnswer = ans; 
			
			System.out.println("Game Answer:"+ gameAnswer);
			System.out.println("player Answer:" + client.playerAnswer);
			
			if(questionSent == 1) {
			if(client.playerAnswer.equals(gameAnswer[1])) {				
	        //prepare a response for the client. 
			response = "[Server:]: That's correct!";
			client.playerPoints++;
			} else {
				response = "[Server:]: That's incorrect!";
				 }
			sendMessageToClient(response, client);
			sendMessageToClient(gameQuestion.get(1), client); //sends next question
			client.playerAnswer = "";
			client.playerAnswer = null;
			response = null;
			questionSent++;
			//} 
			
			client.playerAnswer = ans; 
			
			System.out.println(questionSent);

			if(questionSent == 2) {
			System.out.println("Game Answer:"+ gameAnswer[2]);
			System.out.println("player Answer:" + client.playerAnswer);
			
			if(client.playerAnswer.equals(gameAnswer[2])) {
	        //prepare a response for the client. 
			response = "[Server:]: That's correct!";
			client.playerPoints++;
			} else {
				response = "[Server:]: That's incorrect!";
				 }
			sendMessageToClient(response, client);
			sendMessageToClient(gameQuestion.get(2), client); //sends next question
			client.playerAnswer = null;
			response = null;
			questionSent++;
		    }
			}
		

			if(questionSent == 3) {
			System.out.println("Game Answer:"+ gameAnswer[3]);
			System.out.println("player Answer:" + client.playerAnswer);
			
			if(client.playerAnswer.equals(gameAnswer[3])) {
	        //prepare a response for the client. 
			response = "[Server:]: That's correct!";
			client.playerPoints++;
			} else {
				response = "[Server:]: That's incorrect!";
				 }
			sendMessageToClient(response, client);
			sendMessageToClient(gameQuestion.get(3), client); //sends next question
			client.playerAnswer = null; 
			response = null;
			client.gameStarted = "false"; //Shuts off the game after all questions
		    }
			*/
			sendMessageToClient("You Finished With: " + client.playerPoints + " Points" ,  client); //Displays user points
			
			}
		}
		
		
		

		
		
		
	    //Displays message from client to server
		
		public synchronized void handleMessagesFromClient(String msg, ServerClientManager client) {
			
			// format the client message before displaying in server's terminal output. 
			 String formattedMessage = String.format("[Client %d] : %s", client.getClientID(), msg); 

				if(msg.equals(new String("test"))) { //Tests all of user objects, can be used to display uniqueness of each clients attributes
					System.out.println("Your name is:" + client.playerName2 + " Your Answer is: " + client.playerAnswer + " You Have: " + client.playerPoints + " Points" + "Client game is Started?: " + client.gameStarted); 
				} else
				//Displays message from user to the server
	        display(formattedMessage);
	     
		}
		
		/**
		 * Handles displaying of messages received from each client. 
		 * Called from handleMessagesFromClient()
		 * @param message
		 */
		public void display(String message) {
			System.out.println(">> " + message);
		}
		
		public void displayName(String playerName) {
			System.out.println(playerName);
		}
		
		/**
		 * Gets user's input from the server's command line
		 * sends the user input to all connected clients. 
		 * 
		 * @param msg
		 */
		public void handleUserInput(String msg) {
			

			if(msg.equals(new String("Oover"))) {
				this.stopServer = true;
				close();
				return;
			}			
			Thread[] clientThreadList = getClientConnections();
			for (int i = 0; i < clientThreadList.length; i++) {
				try {
					((ServerClientManager)clientThreadList[i]).sendMessageToClient(msg);
				}
				// Ignore all exceptions when closing clients.
				catch (Exception ex) {
					
				}
			}
		}
		
		/**
		 * Handles, sending a message to client. In this case, it is a string. 
		 * Each client will be calling this to send a message to the client, so it is made synchronized. 
		 * However, this can be handled separately within the ClientManager.
		 * 
		 * @param msg		Message
		 * @param client	Client to be sent
		 */
		
		public synchronized void sendQuestionToClient(String gQuestion, ServerClientManager client) {
			try {
				
				client.sendMessageToClient(gQuestion);
			} catch (IOException e) {
				System.err.println("[server: ] Server-to-client Question sending failed...");
			}
		}
		public synchronized void sendMessageToClient(String msg, ServerClientManager client) {
			try {
				client.sendMessageToClient(msg);
			} catch (IOException e) {
				System.err.println("[server: ] Server-to-client message sending failed...");
			}
		}
		
		
		
		
		
		
		/**
		 * 
		 * @return list of Thread[] pertaining to the clients connected to the server
		 */
		public Thread[] getClientConnections() {
			
			Thread[] clientThreadList = new Thread[clientThreadGroup.activeCount()];
			clientThreadGroup.enumerate(clientThreadList);

			return clientThreadList;
		}
		
		/**
		 * Close the server and associated connections. 
		 */
		public void close() {
			
			if (this.serverSocket == null)
				return;

			try {
				this.stopServer = true;
				this.serverSocket.close();

			} catch (IOException e) {
				System.err.println("[server: ] Error in closing server connection...");
			} finally {

				// Close the client sockets of the already connected clients
				Thread[] clientThreadList = getClientConnections();
				for (int i = 0; i < clientThreadList.length; i++) {
					try {
						((ServerClientManager) clientThreadList[i]).closeAll();
					}
					// Ignore all exceptions when closing clients.
					catch (Exception ex) {
						
					}
				}
				this.serverSocket = null;
				
			}

		}
		
		/**
		 * Represents the thread that listens to the port, and creates client connections. 
		 * Here, each connection is treated as a separate thread, and each client is associated with the ThreadGroup. 
		 * 
		 */
		@Override
		public void run() {
			
			System.out.println("[server: ] starting server: listening @ port: " + port);

			// increments when a client connects. 
			int clientCount = 0;

			// loops until stopserver flag is set to true. 
			while (!this.stopServer) {	
				Socket clientSocket = null;
				try {
					clientSocket = serverSocket.accept();
				} catch (IOException e1) {
					System.err.println("[Server:] Error when handling client connections on port " + port);
				}

				ServerClientManager cm = new ServerClientManager(this.clientThreadGroup, clientSocket, clientCount, this);
				
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					System.err.println("[Server:] server listner thread interruped..");
				}

				clientCount++; //adds to client count when user is connected

			}		
		}

		/**
		 * 
		 * @return returns the status of the server; i.e., whether the server has stopped.
		 */
		public boolean getServerStatus() {
			return this.stopServer;
		}
		
		
		/**
		 * Main() to start the SimpleServer. 
		 * 
		 * @param args
		 * @throws IOException 
		 */
		public static void main(String[] args) throws IOException {

			Server server = new Server();
			// port number to listen
			int port = 7777; //Set server port

			try {
				server.initializeServer(port);

			} catch (IOException e) {
				System.err.println("[Server: ] Error in initializing the server on port " + port);
			}
			// Main thread continues...

			System.out.println("Server is online"); //Outputs when server is running
			
			new Thread(() -> {
				String line = "";
				BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in));
				
				try {
					while(true) {				
						line = consoleInput.readLine();
						server.handleUserInput(line);
						if(server.getServerStatus()) {					
							break;
						}								
					}			
				}
				catch(IOException e) {
					System.out.println("Error in System user input");
				}
				finally {
					try {
						consoleInput.close();
					} catch (IOException e) {
						System.out.println("Error in System user input");
						e.printStackTrace();
					}
				}
			}).start();
			

} //main end

		
}// class end