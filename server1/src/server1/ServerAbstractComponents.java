package server1;

public abstract class ServerAbstractComponents {
	public abstract void handleMessagesFromClient(String msg, ServerClientManager clientmgr);
	public abstract void sendMessageToClient(String msg, ServerClientManager clientmgr);
	public abstract void sendNameToServer2(String pName2, ServerClientManager clientmgr);
	public abstract void sendAnswerToServer(String ans, ServerClientManager clientmgr);
	//public abstract void sendMessageToClient2(String msg2, ServerClientManager clientmgr);
	public abstract void sendPointsToServer(String pPoints, ServerClientManager clientmgr);
	public abstract void sendQuestionToClient(String gQuestion, ServerClientManager clientmgr);
	public abstract void sendPlayerReady(String gStarted, ServerClientManager clientmgr);


//	public abstract void handleNameFromClient(String name, ServerClientManager clientmgr);
}
