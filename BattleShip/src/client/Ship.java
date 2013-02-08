package client;

import javax.swing.JLabel;



/**
 * The Class Ship.
 */
public class Ship extends JLabel{

	/** The parent ship board. */
	private ShipBoard shipBoard;
	
	/** The id of the ship */
	private int id;
	
	/** The cell length of the ship */
	private int length;
	
	/** if ship is horizontal */
	private boolean horizontal; //0-h, 1-v
	
	//position
	/** The start row of the ship */
	private int row;
	
	/** The start column of the ship object. */
	private int column;
	//private Rectangle bounds;
	
	/** The number of hit. */
	private int hit;
	
	/**
	 * Instantiates a new ship object.
	 *
	 * @param sb parent board.
	 * @param id the id of the ship.
	 * @param length the cell length of the ship.
	 * @param row the start row of the ship.
	 * @param column the start column of the ship.
	 */
	public Ship(ShipBoard sb, int id,int length,int row, int column){
		//super("Ship");
		this.shipBoard = sb;
		this.id = id;
		
		this.row = row;
		this.column = column;
		this.length = length;
		this.horizontal = true;
		
		hit = 0;
		//bounds = shipBoard.getShipBounds(row,column,length,orientation);
		this.setBounds(shipBoard.getShipBounds(this));
		//this.setLocation(30,30);
	}

	/**
	 * Gets the id of the ship.
	 *
	 * @return the id of the ship
	 */
	public int getId(){
		return id;
	}

	/**
	 * Gets the cell length.
	 *
	 * @return the length of the ship
	 */
	public int getLength() {
		return length;
	}

	/**
	 * Checks if is horizontal.
	 *
	 * @return true, if is horizontal
	 */
	public boolean isHorizontal() {
		return horizontal;
	}
	
	/**
	 * Sets the horizontal.
	 *
	 * @param h horizontal flag.
	 */
	public void setHorizontal(boolean h){
		horizontal = h;
	}

	/**
	 * Gets the start row of the ship.
	 *
	 * @return the start row 
	 */
	public int getRow() {
		return row;
	}

	/**
	 * Sets the start row.
	 *
	 * @param row start row
	 */
	public void setRow(int row) {
		this.row = row;
	}

	/**
	 * Gets the start column.
	 *
	 * @return start column
	 */
	public int getColumn() {
		return column;
	}

	/**
	 * Sets the start column.
	 *
	 * @param column start column
	 */
	public void setColumn(int column) {
		this.column = column;
	}
	
	/**
	 * Gets the number of hit.
	 *
	 * @return number hit
	 */
	public int getHit(){
		return hit;
	}
	
	/**
	 * Increment hit.
	 */
	public void setHit(){
		hit++;
	}
	
	/**
	 * Reset number of hit.
	 */
	public void resetHit(){
		hit = 0;
	}

}
