package server;

import java.awt.*;
import javax.swing.border.EmptyBorder;
import javax.swing.*;
import java.awt.event.*;
import java.net.*;
import java.util.Date;
import java.io.*;


/**
 * ServerFrame displays server window to start server and display messages.
 */
public class ServerFrame extends JFrame {

	/** The content pane reference. */
	private JPanel contentPane;
	
	DefaultListModel<String> serverLog;
	
	/** clientaccept thread. */
	ClientAccept clientaccept;
	
	/** server socket. */
	protected ServerSocket serverSocket;

	/** gamesession object */
	private GameSession gs;
    
	/**
	 * Create server window.
	 */
	public ServerFrame() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		serverLog = new DefaultListModel();
		JList logList = new JList(serverLog);		
		JScrollPane scrollPane = new JScrollPane(logList);
		contentPane.add(scrollPane, BorderLayout.NORTH);
		
		JButton btnStart = new JButton("Start");
		btnStart.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				startServer();
			}
		});
		contentPane.add(btnStart, BorderLayout.SOUTH);
		
		gs = new GameSession(this);
	}
	
	/**
	 * Start server socket and listen for clients.
	 */
	private void startServer(){
        try {

            // Create a server socket
            serverSocket = new ServerSocket(6020);
            serverLog.addElement(new Date() + ": Server started at socket 6020\n");
            
            clientaccept = ClientAccept.getInstance(this);
            clientaccept.setName("clientAccept");
            clientaccept.start();
            
        }
        catch(IOException ex) {
            System.err.println(ex);
        }		
	}

	/**
	 * handle when players try to connect.
	 *
	 * @param pl client socket
	 */
	protected synchronized void playerJoined(Socket pl){
		serverLog.addElement(new Date() + ": New Player joined ");
		
		if(!gs.playerJoined(pl))
		{
			try {
				ObjectOutputStream pw = new ObjectOutputStream(pl.getOutputStream());
				pw.writeObject("Connection closed.");
				pw.flush();
				pl.close();
				serverLog.addElement(new Date() + ": New Player Closed ");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				serverLog.addElement(new Date() + ": New Player Disconnected ");
				//e.printStackTrace();
			}
		}
	}
	
	/**
	 * Start the server application
	 *
	 * @param args Command arguments.
	 */
	public static void main(String[] args) {
		ServerFrame frame = new ServerFrame();
		frame.setVisible(true);
	}
}
