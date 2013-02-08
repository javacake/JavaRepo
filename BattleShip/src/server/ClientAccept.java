package server;

import java.io.IOException;

/**
 * Thread to accept connection from client sockets. This class implements singleton design pattern to confirm only one instance of the class.
 */
public class ClientAccept extends Thread {
	//singleton
	/** The only instance of this class. */
	private static volatile ClientAccept instance = null;
	
	/** The parent reference */
	ServerFrame sf;
	
	/**
	 * Instantiates a new clientaccept thread.
	 *
	 * @param sf parent reference
	 */
	private ClientAccept(ServerFrame sf){
		this.sf = sf;
	}
	
	/**
	 * Gets reference of single instance of ClientAccept class.
	 *
	 * @param sf parent reference
	 * @return reference of the only instance of this class.
	 */
	public static ClientAccept getInstance(ServerFrame sf) {
        if (instance == null) {
                synchronized (ClientAccept .class){
                        if (instance == null) {
                                instance = new ClientAccept(sf);
                        }
              }
        }
        return instance;
	}
	
	public void run() {
		sf.serverLog.addElement("Wait for players to join game");
		int i = 0;
		while(true){
			try {
				sf.playerJoined(sf.serverSocket.accept());
				//i++;
			} catch (IOException e) {
				sf.serverLog.addElement("Server Socket IO error");
				break;
			}
		}
	}

}
