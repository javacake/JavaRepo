package client;

import java.io.*;
import java.net.Socket;

import server.ClientAccept;
import server.ServerFrame;



/**
 * The Class ServerReader. This will create an saparate process to accept connection from clients. This class implements singleton design pattern to enable it to have only instance through whole program.
 */
public class ServerReader extends Thread {
	//singleton
	/** The only instance of the class */
	private static volatile ServerReader instance = null;
	
	/** The reference to container class. */
	ClientFrame clfrm;
	
	/** The object input stream. */
	ObjectInputStream reader;

	/**
	 * Gets the single instance of ServerReader.
	 *
	 * @param clfrm parent container.
	 * @param reader Object input stream of the server socket.
	 * @return static instance of ServerReader.
	 */
	public static ServerReader getInstance(ClientFrame clfrm, ObjectInputStream reader) {
        if (instance == null) {
                synchronized (ServerReader .class){
                        if (instance == null) {
                                instance = new ServerReader(clfrm,reader);
                        }
              }
        }
        return instance;
	}	
	
	/**
	 * Instantiates a new server reader thread.
	 *
	 * @param clfrm parent container.
	 * @param reader Object input stream.
	 */
	private ServerReader(ClientFrame clfrm, ObjectInputStream reader){
		this.clfrm = clfrm;
		this.reader = reader;
	}
	

    public void run() {
    	//replace with Object
        Object message;
        try {
            while ((message = reader.readObject()) != null) 
            {
            	clfrm.messageReceived(message);
            }

        } catch (ClassNotFoundException e) {

			//e.printStackTrace();
			clfrm.serverDisconnect();
			
		} catch (IOException ex) {
        	//when client is closed or connection error.
			//sf.serverLog.addElement("client disconnected");
			clfrm.serverDisconnect();
			
            //ex.printStackTrace();
        }
    }
}  
