package server1;

public abstract class ServerAbstractComponents {
	public abstract void handleMessagesFromClient(String msg, ServerClientManager clientmgr);
	public abstract void sendMessageToClient(String msg, ServerClientManager clientmgr);
	public abstract void sendNameToServer(String pName, ServerClientManager clientmgr);
//	public abstract void handleNameFromClient(String name, ServerClientManager clientmgr);
}
