package server1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;




public class Server extends ServerAbstractComponents implements Runnable {
	
	// reference variable for server socket. 
		private ServerSocket 			serverSocket;

		// reference variable for ClientHandler for the server. 
		private ServerClientManager 			clientHandler;

		// boolean flag to indicate the server stop. 
		private boolean 				stopServer;

		// reference variable for the Thread
		private Thread 					serverListenerThread;

		// reference variable for ThreadGroup when handling multiple clients
		private ThreadGroup 			clientThreadGroup;

		// variable to store server's port number
		int port;
		
		
		public Server() {
			
			this.stopServer = false;
			
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
	/*	public synchronized void handleNameFromClient(String pName, ServerClientManager client) {
			String clientName = pName; 
		} */
		public String playerName2;
		public synchronized void sendNameToServer2(String pName2, ServerClientManager client) {
			client.playerName2 = pName2;
		}
		
		public String playerPoints;
		public synchronized void sendPointsToServer(String pPoints, ServerClientManager client) {
			client.playerPoints = pPoints;
		}
		
		
		public String playerAnswer;
		public synchronized void sendAnswerToServer(String ans, ServerClientManager client) {
			client.playerAnswer = ans;
			
			String response;
			if(client.playerAnswer.equals("A"))
			{
	        //prepare a response for the client. 
			response = "[server says]: That's correct!";				
			} else {
				response = "[server says]: That's incorrect!";
				
			} sendMessageToClient(response, client);
			
			
		}
		

		
		
		
			
		
		public synchronized void handleMessagesFromClient(String msg, ServerClientManager client) {
			
			// format the client message before displaying in server's terminal output. 
			 String formattedMessage = String.format("[client %d] : %s", client.getClientID(), msg); 

				if(msg.equals(new String("test"))) {
					System.out.println("Your name is:" + client.playerName2 + " Your Answer is: " + client.playerAnswer + " You Have: " + client.playerPoints); //Tests user Name
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
			

			if(msg.equals(new String("over"))) {
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