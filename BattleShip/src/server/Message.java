package server;

import java.io.Serializable;

/**
 * This class wraps all communication types and it is sent and received by client and server to update game play.
 */
public class Message implements Serializable {
	
	/** The serialVersionUID for serializable interface. */
	private static final long serialVersionUID = 1L;	
	
	/** The sender id. */
	private int sender;
	
	/** The type of the message. */
	private PlayerStatus messageType;
	
	/** The ship position. */
	private int[][][] shipPosition;
	
	/** The ship layout. */
	private int[][] shipLocation;
	
	/** The number of row. */
	private int row;
	
	/** The number of column. */
	private int column;
	
	/** The if hip hit or player win. */
	private boolean flag; //hit,win
	
	/** If ship is destroyed. */
	private boolean shipDestroyed;
	
	
	/**
	 * Gets id of sender.
	 *
	 * @return id of sender
	 */
	public int getSender() {
		return sender;
	}
	
	/**
	 * Sets id of sender.
	 *
	 * @param sender id of player
	 */
	public void setSender(int sender) {
		this.sender = sender;
	}
	
	/**
	 * Gets the type of message
	 *
	 * @return the messageType
	 */
	public PlayerStatus getMessageType() {
		return messageType;
	}
	
	/**
	 * Sets the message type.
	 *
	 * @param messageType the messageType to set
	 */
	public void setMessageType(PlayerStatus messageType) {
		this.messageType = messageType;
	}
	
	/**
	 * Gets the ship position.
	 *
	 * @return array of shipPosition
	 */
	public int[][][] getShipPosition() {
		return shipPosition;
	}
	
	/**
	 * Sets the ship position.
	 *
	 * @param shipPosition array of shipPosition to send
	 */
	public void setShipPosition(int[][][] shipPosition) {
		this.shipPosition = shipPosition;
	}
	
	/**
	 * Gets the row number.
	 *
	 * @return the row number.
	 */
	public int getRow() {
		return row;
	}
	
	/**
	 * Sets the row number.
	 *
	 * @param row the row number to send.
	 */
	public void setRow(int row) {
		this.row = row;
	}
	
	/**
	 * Gets the column number.
	 *
	 * @return the column number
	 */
	public int getColumn() {
		return column;
	}
	
	/**
	 * Sets the column number.
	 *
	 * @param column the column to send
	 */
	public void setColumn(int column) {
		this.column = column;
	}
	
	/**
	 * return hit or win flag.
	 *
	 * @return hit or win
	 */
	public boolean isFlag() {
		return flag;
	}
	
	/**
	 * Sets hit or win.
	 *
	 * @param flag hit or win
	 */
	public void setFlag(boolean flag) {
		this.flag = flag;
	}
	
	/**
	 * Gets the ship location array.
	 *
	 * @return shipLocation array
	 */
	public int[][] getShipLocation() {
		return shipLocation;
	}
	
	/**
	 * Sets the ship location array.
	 *
	 * @param shipLocation the shipLocation to set
	 */
	public void setShipLocation(int[][] shipLocation) {
		this.shipLocation = shipLocation;
	}
	
	/**
	 * if ship destroyed.
	 *
	 * @return true, if ship destroyed
	 */
	public boolean isShipDestroyed() {
		return shipDestroyed;
	}
	
	/**
	 * Sets if ship destroyed.
	 *
	 * @param shipDestroyed if ship destroyed
	 */
	public void setShipDestroyed(boolean shipDestroyed) {
		this.shipDestroyed = shipDestroyed;
	}
	

	
}
