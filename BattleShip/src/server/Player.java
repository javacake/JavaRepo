package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * The Player class. this class maintains player's connection and status.
 */
public class Player {

	/** The id of player */
	private int id;
	
	/** The session reference. */
	private GameSession session;
	//comunication
	/** The player's socket. */
	private Socket plSocket;
	
	/** The input stream. */
	private ObjectInputStream fromPlayer;
	
	/** The output stream. */
	private ObjectOutputStream toPlayer;

	//private Thread playerReader;
	/** is player playing. */
	private boolean isPlaying;
	
	// Player Boards
	/** The player's board. */
	private int[][][] playerBoard;
	
	/** The ships's ships. */
	private int[][] ships;
	
	/** Number of ships destroyed. */
	private int shipsDestroyed;

	//Player states
	/** Player's status. */
	private PlayerStatus status;

	
	/**
	 * Instantiates a new player object.
	 *
	 * @param id the id of player
	 * @param plSocket the socket
	 * @param session the session reference
	 */
	public Player(int id, Socket plSocket, GameSession session) {
		this.id = id;
		this.plSocket = plSocket;
		this.session = session;
		
        try {
			//fromPlayer1 = new DataInputStream(player1.getInputStream());
        	toPlayer = new ObjectOutputStream(plSocket.getOutputStream());
        	toPlayer.flush();
        	fromPlayer = new ObjectInputStream(plSocket.getInputStream());
	        
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        status = PlayerStatus.CONNECTED;
        shipsDestroyed = 0;
	}
	
	/**
	 * Reset player.
	 */
	public void reset(){
		//private int[][][] playerBoard;
		//private int[][] ships;
        status = PlayerStatus.CONNECTED;
		shipsDestroyed = 0;
	}
	
	/**
	 * Gets player id.
	 *
	 * @return id
	 */
	public int getId(){
		return id;
	}

	/**
	 * Checks if player is playing.
	 *
	 * @return isPlaying
	 */
	public boolean isPlaying() {
		return isPlaying;
	}

	/**
	 * Sets if player is playing.
	 *
	 * @param isPlaying if player is Playing
	 */
	public void setPlaying(boolean isPlaying) {
		this.isPlaying = isPlaying;
	}

	/**
	 * Gets the player board.
	 *
	 * @return playerBoard
	 */
	public int[][][] getPlayerBoard() {
		return playerBoard;
	}

	/**
	 * Sets the player board.
	 *
	 * @param playerBoard playerBoard
	 */
	public void setPlayerBoard(int[][][] playerBoard) {
		this.playerBoard = playerBoard;
	}

	/**
	 * Gets ships of player.
	 *
	 * @return ships
	 */
	public int[][] getShips() {
		return ships;
	}

	/**
	 * Sets ships of player.
	 *
	 * @param ships ships
	 */
	public void setShips(int[][] ships) {
		this.ships = ships;
	}

	/**
	 * Gets number of ships destroyed.
	 *
	 * @return shipsDestroyed
	 */
	public int getShipsDestroyed() {
		return shipsDestroyed;
	}

	/**
	 * Sets number of ships destroyed.
	 *
	 */
	public void setShipsDestroyed() {
		this.shipsDestroyed++;
	}

	/**
	 * Gets the status of player.
	 *
	 * @return status
	 */
	public PlayerStatus getStatus() {
		return status;
	}

	/**
	 * Sets the status of player.
	 *
	 * @param status status
	 */
	public void setStatus(PlayerStatus status) {
		this.status = status;
	}

	/**
	 * Gets the input stream
	 *
	 * @return input stream
	 */
	public ObjectInputStream getFromPlayer() {
		return fromPlayer;
	}

	/**
	 * Gets the output stream.
	 *
	 * @return output stream.
	 */
	public ObjectOutputStream getToPlayer() {
		return toPlayer;
	}
	
	/**
	 * Sets hit at cell.
	 *
	 * @param row row
	 * @param column column
	 */
	public void setHitAtCell(int row,int column){
        
		playerBoard[row][column][1] = 1; //isHit
		
//		if(flags[row][column][0] > 0){
//				cells[row][column].setShipHit();
//				gameBoard.setShipHitById(flags[row][column][0]);
//				
//		        System.out.println("Ship id hit:" + flags[row][column][0]);
//		}
		
		//return flags[row][column][0];
	}	
	
}
