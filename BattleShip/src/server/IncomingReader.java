package server;

import java.io.*;
import java.net.Socket;

/**
 * The runnable class to receive client message asynchroniously. 
 */
public class IncomingReader implements Runnable {
	
	/** The gamesession reference. */
	GameSession gameSession;
	
	/** The object input stream. */
	ObjectInputStream reader;
	
	/** The player id. */
	int playerId;
	
	/**
	 * Instantiates a new IncomingReader.
	 *
	 * @param gameSession gamesession reference.
	 * @param reader object input stream.
	 * @param playerId id of player.
	 */
	public IncomingReader(GameSession gameSession, ObjectInputStream reader, int playerId){
		this.gameSession = gameSession;
		this.reader = reader;
		this.playerId = playerId;
	}
	
	
    public void run() {
    	//replace with Object
        Object message;
        try {
            while ((message = reader.readObject()) != null) 
            {
            	gameSession.messageReceived(playerId, message);
            }

        } catch (ClassNotFoundException e) {
			//e.printStackTrace();
			gameSession.playerDisconnect(playerId);
			
		} catch (IOException ex) {
        	//when client is closed or connection error.
			//sf.serverLog.addElement("client disconnected");
			gameSession.playerDisconnect(playerId);
			
            //ex.printStackTrace();
        }
    }
}  
