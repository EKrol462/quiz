package server1;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;


//import server1clients.ServerClient;
/**
* @author Eryk Krol st20124378
* @version 06/01/2020
*/
//import server1clients.ServerClient;

public class ServerClientManager extends Thread {
	
	// reference variable to store client socket
		private Socket 					clientSocket;
		
		// reference for the Sever
		private ServerAbstractComponents	server;
		
		// boolean flag to indicate whether to stop the connection
		private boolean					stopConnection;
		
		// Input Output streams to communicate with the client using Serialized objects
		private ObjectOutputStream 		out;
		private ObjectInputStream 		in;
		
		// store an incrementing ID for the client. 
		private int 					clientID;
		
		
		//Store Declared Player Information from ServerClient Class
		public String playerName;
		public String playerName2;
		public String playerAnswer;
		public int playerPoints;
		//public String pName = playerName;
		public String gameQuestion;

		public String message;

		public String gameStarted;
		
				

		
		/**
		 * Constructor to be called, when handling multiple clients. Requires a ThreadGroup instance from the Server
		 * 
		 * @param threadgroup
		 * @param socket
		 * @param clientID
		 * @param server
		 * @param playerName
		 */
		     public ServerClientManager(ThreadGroup threadgroup, Socket socket, int clientID, ServerAbstractComponents server) {
			//public ServerClientManager(ThreadGroup threadgroup, Socket socket, int clientID, ServerAbstractComponents server) {

			super(threadgroup, (Runnable) null);
			
			this.clientSocket = socket;
			this.server = server;
			this.stopConnection = false;
			this.clientID = clientID;
			
			
			
			System.out.println("[ClientManager: ] new client request received, port " 
					+ socket.getPort());
			try {
				this.out = new ObjectOutputStream(this.clientSocket.getOutputStream());
				this.in = new ObjectInputStream(this.clientSocket.getInputStream());			
			}
			catch(IOException e) {
				System.err.println("[ClientManager: ] error when establishing IO streams on client socket.");
				try {
					closeAll();
				} catch (IOException e1) {
					System.err.println("[ClientManager: ] error when closing connections..." + e1.toString());
					

				}
			}
			
			start();	
		}
		
		/**
		 * Performs the function of sending a message from Server to remote Client#
		 * Uses ObectOutputStream 
		 * 
		 * @param msg
		 * @throws IOException
		 */
		     
		
		 	public void sendQuestionToClient(String gQuestion) throws IOException {
				if (this.clientSocket == null || this.out == null)
					throw new SocketException("socket does not exist");
				
				this.out.writeObject(gQuestion);
			} 
		     
		  public void sendMessageToClient(String msg) throws IOException {
			  if (this.clientSocket == null || this.out == null)
				throw new SocketException("socket does not exist");
			
			
			this.out.writeObject(msg);
		}
		
	
		
		/*Custom message from server
		public void sendMessageToClient2(String msg2)  throws IOException {
			if (this.clientSocket == null || this.out == null)
				throw new SocketException("socket does not exist");
			
			
			this.out.writeObject(msg2);
		} */
		
		/**
		 * Closes all connections for the client. 
		 * @throws IOException
		 */
		public void closeAll() throws IOException {
			try {
				// Close the socket
				if (this.clientSocket != null)
					this.clientSocket.close();

				// Close the output stream
				if (this.out != null)
					this.out.close();

				// Close the input stream
				if (this.in != null)
					this.in.close();
			} finally {
				// Set the streams and the sockets to NULL no matter what.

				this.in = null;
				this.in = null;
				this.clientSocket = null;
				
			}
		}

		/**
		 * Receive messages (String) from the client, passes the message to Sever's handleMessagesFromClient() method.
		 * Works in a loop until the boolean flag to stop connection is set to true. 
		 */
		@Override
		public void run() {
			
			// Objects incoming from the user
			
			String msg = ""; //Message
			String pName2 = null; //player name
		    String ans = ""; //player answer 
			String pPoints = "0"; //player points
			String gStarted = "false"; //player started game true/false (string instead of boolean as the server does not currently support other object types.
		
			//Player points  object
			try {
			pPoints = (String)this.in.readObject();
		} catch (ClassNotFoundException e2) {
			System.out.println("Error, points class not found");
			e2.printStackTrace();
		} catch (IOException e2) {
			System.out.println("Error sending player points to server");
			e2.printStackTrace();
		}
		this.server.sendPointsToServer(pPoints, this);
			
			
			//Player Name
			try {
			pName2 = (String)this.in.readObject();
		} catch (ClassNotFoundException e2) {
			System.out.println("Error, Name class not found");
			e2.printStackTrace();
		} catch (IOException e2) {
			System.out.println("Error sending player name to server");
			e2.printStackTrace();
		}
		this.server.sendNameToServer2(pName2, this);
		
		
		//Game Started boolean
		try {
			gStarted = (String)this.in.readObject();
		} catch (ClassNotFoundException e1) {
			System.out.println("Error, Game Started class not found");
			e1.printStackTrace();
		} catch (IOException e1) {
			System.out.println("Error sending player answer");
			e1.printStackTrace();
		}
		this.server.sendPlayerReady(gStarted,this);
		
		
		if(gStarted.equals("true")) { //TODO implement working "while gStarted
		
			//Player Answer to the class
	
			//for (int i = 0; i < 5; i++) {
		try {
			ans = (String) this.in.readObject();
		} catch (ClassNotFoundException e2) {
			System.out.println("Error, answer class not found");
			e2.printStackTrace();
		} catch (IOException e2) {
			System.out.println("Error sending player answer to server");
			e2.printStackTrace();
		}
		this.server.sendAnswerToServer(ans, this);
			}
	    //  }	
		//}
		
			try {
				while (!this.stopConnection) {
					// This block waits until it reads a message from the client
					// and then sends it for handling by the server,
					// statement until something is received from the server
					 
					
					//message
					msg = (String)this.in.readObject();
					this.server.handleMessagesFromClient(msg, this);
					
					if(msg.equals("over")) {
						this.stopConnection = true;					
					}				
				}
				
				System.out.println("[ClientManager: ] stopping the client connection ID: " + this.clientID);
			} catch (Exception e) {
				System.err.println("[ClientManager: ] error when reading message from client.." + e.toString());
				/**
				 * If there is an error, while the connection is not stopped, close all. 
				 */
				if (!this.stopConnection) {
					try {
						closeAll();
					} 
					catch (Exception ex) 
					{
						System.err.println("[ClientManager: ] error when closing the connections.." + ex.toString());
					}
				}
			}
			finally {
				if(this.stopConnection) {
					try {
						closeAll();
					} catch (IOException e) {
						System.err.println("[ClientManager: ] error when closing the connections.." + e.toString());
					}				
				}
			}
		}	

		/**
		 * @return a description of the client, including IP address and host Name to the server
		 */
		public String toString() {
			return this.clientSocket == null ? null : this.clientSocket.getInetAddress().getHostName() + " ("
					+ this.clientSocket.getInetAddress().getHostAddress() + ")";
		}
		
		
		//Getter for the client ID
		public int getClientID() {
			return this.clientID;
		}	
		

	}
