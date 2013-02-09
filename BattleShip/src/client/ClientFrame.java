package client;

import javax.swing.*;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

import server.Message;
import server.PlayerStatus;



/**
 * The Class ClientFrame.
 * 
 * This class create client's window containing two grids with five ships.
 */
public class ClientFrame extends JFrame{
	
	/** The client socket. */
	private Socket clientSocket;
	
	/** The reader thread. */
	private ServerReader reader;
	
	/** The writer stream. */
	private ObjectOutputStream writer;
	
	/** The player status. */
	private PlayerStatus status;
	
	/** The player board. */
	private GameBoard player;
	
	/** The opponent board. */
	private GameBoard opponent;
	
	/** The is connected flag. */
	private boolean isConnected;
	
	/** The player win flag. */
	private boolean playerWin;
	
	/** The game over flag. */
	private boolean gameOver;
	
	/** The player. */
	private JLabel lblPlayer;
	
	/** The opponent. */
	private JLabel lblOpponent;
	
	/** The player's ship left. */
	private JLabel lblShipLeft;
	
	/** The opponent's ship left. */
	private JLabel lblShipLeft_1;	
	
	/** The player status. */
	private JLabel lblStatus;
	
	/** The opponent status. */
	private JLabel lblStatus_1;	
	
	/** The player message. */
	private JLabel lblMessage;
	
	/** The opponent message. */
	private JLabel lblMessage_1;	
	
	/** The connect button. */
	private JButton btnConnect;
	
	/** The play button. */
	private JButton btnPlay;
	
	/** The new game button. */
	private JButton btnNewGame;
	
	
	/**
	 * Create a new client frame.
	 *
	 * @param title title of the window
	 */
	public ClientFrame(String title){
		super(title);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setLayout(null);
		
		isConnected = false;
		gameOver = false;
		playerWin = false;
		
		player = new GameBoard(this);
		player.setBackground(Color.LIGHT_GRAY);
		player.setBounds(10, 150, 300, 300);
		getContentPane().add(player);
		
		/*
		 * Opponent's ships are invisible
		 * only show destroyed ship
		 */
		opponent = new GameBoard(this);
		opponent.setBackground(Color.LIGHT_GRAY);
		opponent.setBounds(320, 150, 300, 300);
		getContentPane().add(opponent);
		opponent.disableMouseEvents(2);
		opponent.hideAllShip();
		
		lblPlayer = new JLabel("Player");
		lblPlayer.setBounds(10, 11, 300, 14);
		getContentPane().add(lblPlayer);
		
		lblOpponent = new JLabel("Opponent");
		lblOpponent.setBounds(320, 11, 300, 14);
		getContentPane().add(lblOpponent);
		
		lblShipLeft = new JLabel("Ship Destroyed:");
		lblShipLeft.setBounds(10, 36, 300, 14);
		getContentPane().add(lblShipLeft);
		
		lblShipLeft_1 = new JLabel("Ship Desroyed:");
		lblShipLeft_1.setBounds(320, 36, 300, 14);
		getContentPane().add(lblShipLeft_1);
		
		lblStatus = new JLabel("Status");
		lblStatus.setBounds(10, 61, 300, 14);
		getContentPane().add(lblStatus);
		
		lblMessage_1 = new JLabel("Message");
		lblMessage_1.setBounds(320, 86, 300, 14);
		getContentPane().add(lblMessage_1);
		
		lblMessage = new JLabel("Message");
		lblMessage.setBounds(10, 86, 300, 14);
		getContentPane().add(lblMessage);
		
		lblStatus_1 = new JLabel("Status");
		lblStatus_1.setBounds(320, 61, 300, 14);
		getContentPane().add(lblStatus_1);
		
		btnConnect = new JButton("Connect");
		btnConnect.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				connectToServer();
			}
		});
		btnConnect.setBounds(10, 111, 89, 23);
		getContentPane().add(btnConnect);
		
		btnPlay = new JButton("Play");
		btnPlay.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				startGame();
			}
		});
		btnPlay.setEnabled(false);
		btnPlay.setBounds(109, 111, 89, 23);
		getContentPane().add(btnPlay);
		
		btnNewGame = new JButton("New Game");
		btnNewGame.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				newGame();
			}
		});
		btnNewGame.setEnabled(false);
		btnNewGame.setBounds(208, 111, 89, 23);
		getContentPane().add(btnNewGame);

		//pack();
		player.disableMouseEvents(1);
		opponent.disableMouseEvents(2);
		
		status = PlayerStatus.NOT_CONNECTED;
		lblStatus.setText("Not Connected");
		lblShipLeft.setText("Ship Destroyed: " + player.getShipDestroyed());
		lblShipLeft_1.setText("Ship Destroyed: " + opponent.getShipDestroyed());
		
	}
	
	/**
	 * Sets the opponent ship position.
	 *
	 * @param flags integer array of ship position.
	 */
	public void setOpponentShipPosition(int[][][] flags){
		opponent.setOpponentShipPosition(flags);
	}
	
	/**
	 * Gets the player's ship position.
	 *
	 * @return player's ship position array.
	 */
	public int[][][] getMyShipPosition(){
		return player.getMyShipPosition();
	}
	
	/**
	 * Sets the opponent ship location.
	 *
	 * @param shipLocation opponent's ship layout.
	 */
	public void setOpponentShipLocation(int[][] shipLocation){
		opponent.setOpponentShipLocation(shipLocation);
	}
	
	/**
	 * Gets the player's ship location.
	 *
	 * @return player's ship layout.
	 */
	public int[][] getMyShipLocation(){
		return player.getMyShipLocation();
	}
	
	/**
	 * Connect to server socket.
	 */
	public void connectToServer(){
		//Connect Button Clicked
		
		//start a socket thread, reader and writer thread
		//Notify user connection status
		lblMessage.setText("trying to connect\n");
		if(setUpNetworking()) {
			lblMessage.setText("Set Ships and start game\n");
			player.enableMouseEvents(1);
			btnPlay.setEnabled(true);
			btnConnect.setEnabled(false);
			status = PlayerStatus.CONNECTED;
			lblStatus.setText("Connected");
		}
	}
	
	/**
	 * Send player's ship position and layout and start new game.
	 */
	public void startGame(){
		//Start Button Clicked

		player.disableMouseEvents(1);
		
		//Send ship flags to server
		//wait for ship flags to arrive
		Message msg = new Message();
		msg.setMessageType(PlayerStatus.READY);
		msg.setShipPosition(getMyShipPosition());
		msg.setShipLocation(getMyShipLocation());
		sendMessage(msg);
		
		status = PlayerStatus.READY;
		lblStatus.setText("Ready");
		lblMessage.setText("Waiting for opponent");
		btnPlay.setEnabled(false);
	}
	
	/**
	 * Reset Games and game boards.
	 */
	public void newGame(){
		
		player.resetGameBoard();
		opponent.resetGameBoard();
		opponent.hideAllShip();
		
		player.enableMouseEvents(1);
		opponent.disableMouseEvents(2);
		
		btnPlay.setEnabled(true);
		btnNewGame.setEnabled(false);
		
		lblMessage.setText("Set Ships and start game\n");
		lblStatus.setText("Conneted");
		lblShipLeft.setText("Ship Destroyed: " + player.getShipDestroyed());
		lblShipLeft_1.setText("Ship Destroyed: " + opponent.getShipDestroyed());
		
		status = PlayerStatus.CONNECTED;
		Message msg = new Message();
		msg.setMessageType(PlayerStatus.CONNECTED);
		sendMessage(msg);
		
		repaint();
	}

    /**
     * Create socket and connect to server.
     *
     * @return true, if successful
     */
    private boolean setUpNetworking() {
        try {
        	clientSocket = new Socket(InetAddress.getLocalHost(), 6020);
        	//clientSocket = new Socket("192.168.0.18", 6020);
        	
        	if(clientSocket.isConnected()){
	            writer = new ObjectOutputStream(clientSocket.getOutputStream());
	            writer.flush();
	            
	            reader = ServerReader.getInstance(this, new ObjectInputStream(clientSocket.getInputStream()));
	            reader.start();
	            
	            return true;
        	}
        }
        catch(IOException ex)
        {
            //ex.printStackTrace();
        	lblMessage.setText("Server is not running\n");
        }
        lblMessage.setText("networking could not established\n");
        return false;
    }
    
	/**
	 * Game over status.
	 *
	 * @param win Set if game won or lost.
	 */
	public void gameOverStatus(boolean win){
		gameOver = true;
		this.playerWin = win; 		
	}	
	
	/**
	 * Message object received from server.
	 *
	 * @param message the Message object.
	 */
	public void messageReceived(Object message) {
		// TODO Auto-generated method stub
		Message srMsg = (Message) message;
		updateClient(srMsg);
		
		//lblMessage.setText("Message received " + srMsg.getMessageType());
		
	}
	
	/**
	 * Update the client with received message.
	 *
	 * @param srMsg the Message Object.
	 */
	private void updateClient(Message srMsg) {
		
		switch(srMsg.getMessageType()){
		case NOT_CONNECTED:
			lblStatus_1.setText("Not Connected");
			break;
		case CONNECTED:
			lblStatus_1.setText("Connected");
			break;			
		case READY:
			//Set oponent ships
			lblStatus_1.setText("Ready");
			setOpponentShipPosition(srMsg.getShipPosition());
			setOpponentShipLocation(srMsg.getShipLocation());			
			
			break;
		case TURN:
			//if first turn game start
			if(status == PlayerStatus.READY){
				setOpponentShipPosition(srMsg.getShipPosition());
				setOpponentShipLocation(srMsg.getShipLocation());
				
				lblStatus.setText("Game Started");
				lblStatus_1.setText("Game Started");	
			}
			
			status = PlayerStatus.TURN;
			opponent.enableMouseEvents(2);
			if(srMsg.isFlag()){
			//with coordinate
				player.setHitAtCell(srMsg.getRow(), srMsg.getColumn());	
			}
			
			lblShipLeft.setText("Ship Destroyed: " + player.getShipDestroyed());
			lblShipLeft_1.setText("Ship Destroyed: " + opponent.getShipDestroyed());
			lblMessage.setText("Your turn");
			lblMessage_1.setText("Waiting for your turn");
			
			break;
		case WAIT:
			if(status == PlayerStatus.READY){				
				lblStatus.setText("Game Started");
				lblStatus_1.setText("Game Started");	
			}
			
			status = PlayerStatus.WAIT;
			opponent.disableMouseEvents(2);			
			if(srMsg.isFlag()){
			//with coordinate
				player.setHitAtCell(srMsg.getRow(), srMsg.getColumn());
			}
			
			lblShipLeft.setText("Ship Destroyed: " + player.getShipDestroyed());
			lblShipLeft_1.setText("Ship Destroyed: " + opponent.getShipDestroyed());
			
			lblMessage.setText("Waiting for opponent's turn");
			lblMessage_1.setText("Opponent's turn");
			
			break;
		case GAMEOVER:
			status = PlayerStatus.GAMEOVER;
			opponent.disableMouseEvents(2);
			btnNewGame.setEnabled(true);
			
			lblStatus.setText("Game Over");
			lblStatus_1.setText("Game Over");
			
			if(srMsg.isFlag()){
				lblMessage.setText("Player won!");
				lblMessage_1.setText("Opponent lost");
			}else{
				lblMessage_1.setText("Opponent won!");
				lblMessage.setText("Player lost");				
			}
		}
	}

	/**
	 * Opponent move action.
	 *
	 * @param row the row of the move.
	 * @param column the column of the move.
	 * @param flag If any ship hit.
	 * @param sdflag If ship destroyed.
	 */
	public void opponentMouseHit(int row, int column, boolean flag, boolean sdflag){
		Message msg = new Message();
		msg.setMessageType(PlayerStatus.TURN);
		msg.setRow(row);
		msg.setColumn(column);
		msg.setFlag(flag);
		msg.setShipDestroyed(sdflag);
		//System.out.println("Messgae sent " + row + ":" + column + ":" + flag);
		
		opponent.disableMouseEvents(2);
		sendMessage(msg);
	}
	
	/**
	 * Send Message object to server.
	 *
	 * @param msg the Message object to send.
	 */
	private void sendMessage(Message msg){
		try {
			writer.writeObject(msg);
		} catch (IOException e) {
			lblStatus.setText("Conneection Error");
		}
	}
	
	/**
	 * Handles server connection error and resets client.
	 */
	public void serverDisconnect() {
		// TODO Auto-generated method stub
		lblStatus.setBackground(Color.red);
		lblStatus.setText("Disconnected");

		lblStatus_1.setBackground(Color.red);
		lblStatus_1.setText("Disconnected");
		
		newGame();
		clientSocket = null;
		btnPlay.setEnabled(false);
		btnConnect.setEnabled(true);
		//stop and reset game
		//reset socket
	}		
		
	/**
	 * The main method to start the client program.
	 *
	 * @param args the command arguments.
	 */
	public static void main(String[] args) {

		ClientFrame frame = new ClientFrame("BattleShip Client");
		// Display the frame
		frame.setSize(640, 500); //300,300
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

	}




}
