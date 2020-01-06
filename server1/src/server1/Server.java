package server1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;




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
			 * Initializes the ThreadGroup. 
			 * Use of a ThreadGroup is easier when handling multiple clients, although it is not a must. 
			 */
			this.clientThreadGroup = new ThreadGroup("ClientManager threads");
			
		}
		
		/**
		 * Initializes the server. Takes port number, creates a new server socket instance. 
		 * Starts the server's listening thread. 
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
  
		/**
		 * handles messages from each client. In this case messages are simply displayed. 
		 * Modified to prepare a response and send back to the same client. Simply changes the input text to upper case. 
		 * This is a shared resource among all client threads, so it has to be synchronized.
		 * 
		 * 
		 * @param msg
		 * @param client
		 */

		

			
		
		
		
		public String playerName2;
		public synchronized void sendNameToServer2(String pName2, ServerClientManager client) {
			client.playerName2 = pName2;
		
		  	}
		
		public String playerPoints;
		public synchronized void sendPointsToServer(String pPoints, ServerClientManager client) {
			client.playerPoints = pPoints;
		}
		
		
		
		
	/*	public synchronized void sendQuestionToClient (String gQuestion, ServerClientManager client) {
		gQuestion = gameQuestion;
		try {
			client.sendQuestionToClient(gQuestion);
			this.output.writeObject(gQuestion);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		} */
		
		public String [] gameQuestion = {"A czy B?", "C czy D?"};
		
		public String gameStarted;
		int questionSent = 0 ;
		public synchronized void sendPlayerReady(String gStarted, ServerClientManager client) {
			client.gameStarted = gStarted;


			if (client.gameStarted.equals("true") )
			{
			if(questionSent == 0) {
			sendMessageToClient(gameQuestion[0], client);
			}
			
			if(questionSent == 1) {
			sendMessageToClient(gameQuestion[1], client);
			} 
			} 
				}
		
		public String playerAnswer[];
		public String [] gameAnswer = {"A", "A"};
		public synchronized void sendAnswerToServer(String ans[], ServerClientManager client) {
		/*	if (client.gameStarted.equals("true") )
			{
			for (int i = 0; i < 5;) {
				 client.playerAnswer[i] = ans[i];
				 String response;
				 sendMessageToClient(gameQuestion[0], client);
				 System.out.println("Game Answer:"+ gameAnswer[i]);
				 System.out.println("player Answer:" + client.playerAnswer[i]);
			     
			 if(client.playerAnswer[i].equals(gameAnswer[i])) {
		        //prepare a response for the client. 
				response = "[server says]: That's correct!";				
				} else {
					response = "[server says]: That's incorrect!";
					 }
				sendMessageToClient(response, client);
				
				client.playerAnswer[i] = "";
		} 
			}
			client.gameStarted = "false"; */
			//TODO Make a for loop
			client.playerAnswer[0] = ans[0]; 
			String response;
			System.out.println("Game Answer:"+ gameAnswer[0]);
			System.out.println("player Answer:" + client.playerAnswer[0]);
			
			if(client.playerAnswer[0].equals(gameAnswer[0]))
			{
	        //prepare a response for the client. 
			response = "[server says]: That's correct!";				
			} else {
				response = "[server says]: That's incorrect!";
				 }
			sendMessageToClient(response, client);
			client.playerAnswer[0] = null;
			response = null;
			questionSent++;
			
			
			
			client.playerAnswer[1] = ans[1]; 
			
			if(questionSent == 1) {
			System.out.println("Game Answer:"+ gameAnswer[1]);
			System.out.println("player Answer:" + client.playerAnswer[1]);
			
			if(client.playerAnswer[1].equals(gameAnswer[1])) {
	        //prepare a response for the client. 
			response = "[server says]: That's correct!";				
			} else {
				response = "[server says]: That's incorrect!";
				 }
			client.playerAnswer[1] = "";
			sendMessageToClient(response, client);
			client.playerAnswer[1] = null;
			response = null;
			questionSent++;
			} 
			
			
			
			
		
		
		
		
		}	
		
		

		
		
		
			
		
		public synchronized void handleMessagesFromClient(String msg, ServerClientManager client) {
			
			// format the client message before displaying in server's terminal output. 
			 String formattedMessage = String.format("[client %d] : %s", client.getClientID(), msg); 

				if(msg.equals(new String("test"))) {
					System.out.println("Your name is:" + client.playerName2 + " Your Answer is: " + client.playerAnswer + " You Have: " + client.playerPoints + " Points" + "Client game is Started?: " + client.gameStarted); //Tests user Name
				} else if (msg.equals(new String("ready"))) {
					sendMessageToClient("Player " + client.playerName2 + " is Ready", client); //TODO delete
				} else
				
	        display(formattedMessage);
	      
		/*	if(client.playerAnswer == "A")
			{
	        //prepare a response for the client. 
			String response = "[server says]: That's correct!";				
			sendMessageToClient(response, client);
			} else {
				String response = "[server says]: That's incorrect!";
			}  */
			
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
					System.err.println("[server: ] Error when handling client connections on port " + port);
				}

				ServerClientManager cm = new ServerClientManager(this.clientThreadGroup, clientSocket, clientCount, this);
				//	ClientManager cm = new ClientManager(this.clientThreadGroup, clientSocket, clientCount, this);

				// new ClientManager(clientSocket, this);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					System.err.println("[server: ] server listner thread interruped..");
				}

				clientCount++;

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
			int port = 7777; //Integer.parseInt(args[0]); 

			try {
				server.initializeServer(port);

			} catch (IOException e) {
				System.err.println("[server: ] Error in initializing the server on port " + port);
			}
			// Main thread continues...

			System.out.println("get user input...");
			System.out.println("Hello World");
			
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
					System.out.println("Error in System.in user input");
				}
				finally {
					try {
						consoleInput.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}).start();
			

} //main end

		
}// class end