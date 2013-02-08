package client;

import java.awt.Color;
import java.awt.Rectangle;

import javax.swing.JPanel;


/**
 * The Class ShipBoard. This class contains visible ships. It displays and hides ships during game play.
 */
public class ShipBoard extends JPanel{
	
	/** The parent game board. */
	private GameBoard gameBoard;

	/** The array of ships. */
	private Ship[] battleShips;
	
	/**
	 * Instantiates a ship board.
	 *
	 * @param gb parent game board.
	 */
	public ShipBoard(GameBoard gb){
		this.setLayout(null);
		this.gameBoard = gb;
		
		battleShips = new Ship[5];
		
		for(int i = 0;i<5;i++){
			int size = ((5 - i) < 3) ? 6-i : 5-i;
			battleShips[i] = new Ship(this,i+1,size,i,0);
			battleShips[i].setOpaque(true);
			this.add(battleShips[i]);
		}

		battleShips[0].setBackground(Color.green.darker().darker());
		battleShips[1].setBackground(Color.magenta.darker());
		battleShips[2].setBackground(Color.orange.darker());
		battleShips[3].setBackground(Color.pink.darker().darker());
		battleShips[4].setBackground(Color.yellow.darker().darker());

	}

	/**
	 * Gets ship's bounds.
	 *
	 * @param s ship object.
	 * @return ship's bounds.
	 */
	public Rectangle getShipBounds(Ship s) {
		return gameBoard.getShipBounds(s);		
	}

	/**
	 * Hides all ship on the current board.
	 */
	public void hideAllShip(){
		for(int i = 0;i<5;i++){
			battleShips[i].setOpaque(false);
		}
		repaint();
	}
	
	/**
	 * Hide ship of specified id.
	 *
	 * @param shipId id of the ship to hide.
	 */
	public void hideShip(int shipId){
		battleShips[shipId-1].setOpaque(false);
		repaint();
	}
	
	/**
	 * Show ship of specified id.
	 *
	 * @param shipId id of the ship to show.
	 */
	public void showShip(int shipId){
		battleShips[shipId-1].setOpaque(true);
		repaint();
	}
	
	/**
	 * Sets ship hit flag of specified ship id.
	 *
	 * @param shipId id of ship.
	 * @return true, if ship hit.
	 */
	public boolean setShipHitById(int shipId){
		battleShips[shipId-1].setHit();
		if(battleShips[shipId-1].getHit()==battleShips[shipId-1].getLength()){
			return true;
		}
		else
			return false;
	}

	/**
	 * Sets the opponent's ship location.
	 *
	 * @param shipLocation array of opponent ship location
	 */
	public void setOpponentShipLocation(int[][] shipLocation){
		for(int i = 0;i<5;i++){
			
			battleShips[i].setHorizontal(shipLocation[i][0]==0);
			battleShips[i].setRow(shipLocation[i][1]);
			battleShips[i].setColumn(shipLocation[i][2]);
			
			battleShips[i].setBounds(getShipBounds(battleShips[i]));
			
		}
		//repaint();
	}
	
	/**
	 * Gets player's ship location.
	 *
	 * @return player's ship location
	 */
	public int[][] getMyShipLocation(){
		int[][] shipLocation = new int[5][3];
		for(int i = 0;i<5;i++){
			shipLocation[i][0] = battleShips[i].isHorizontal()?0:1;
			shipLocation[i][1] = battleShips[i].getRow();
			shipLocation[i][2] = battleShips[i].getColumn();
			//battleShips[i].setBounds(getShipBounds(battleShips[i]));
		}
		return shipLocation;
	}
	
	/**
	 * Reset current ship board.
	 */
	public void resetShipBoard() {
		for(int i = 0;i<5;i++){
			battleShips[i].setHorizontal(true);
			battleShips[i].setRow(i);
			battleShips[i].setColumn(0);
			battleShips[i].resetHit();
			battleShips[i].setBounds(getShipBounds(battleShips[i]));
		}
		repaint();
	}
	

}
