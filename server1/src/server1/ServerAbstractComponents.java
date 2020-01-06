package server1;

/**
* @author Eryk Krol st20124378
* @version 06/01/2020
*/

//Abstract methods

public abstract class ServerAbstractComponents {
	public abstract void handleMessagesFromClient(String msg, ServerClientManager clientmgr);
	public abstract void sendMessageToClient(String msg, ServerClientManager clientmgr);
	public abstract void sendNameToServer2(String pName2, ServerClientManager clientmgr);
	public abstract void sendAnswerToServer(String ans, ServerClientManager clientmgr);
	public abstract void sendPointsToServer(String pPoints, ServerClientManager clientmgr);
	public abstract void sendQuestionToClient(String gQuestion, ServerClientManager clientmgr);
	public abstract void sendPlayerReady(String gStarted, ServerClientManager clientmgr);


//	public abstract void handleNameFromClient(String name, ServerClientManager clientmgr);
}
