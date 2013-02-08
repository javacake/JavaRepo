package client;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.LineBorder;



/**
 * The Class Cell.
 *  * Class to create cell inside grid of game board.
 */
public class Cell extends JPanel {

	// Indicate the row and column of this cell in the board
    /** The row. */
	private int row;
    
    /** The column. */
    private int column;
	
	/** The is mouse on. */
	private int isMouseOn;
    // Token used for this cell
    /** The ship hit. */
    private int shipHit; //0-nohit 1-noshiphit 2-shiphit

    /** The grid board. */
    private GridBoard gridBoard;
    
    /** The cell mouse listener. */
    private ClickListener cellMouseListener;

    /**
     * Creates a new cell.
     *
     * @param row set row number of the cell.
     * @param column set column number of the cell.
     * @param gui reference to the parent.
     */
    public Cell(int row, int column, GridBoard gui) {

      this.row = row;
      this.column = column;
      this.gridBoard = gui;
      isMouseOn = 0;
      shipHit = 0;
      this.setPreferredSize(new Dimension(30,30));
      this.setMinimumSize(new Dimension(30, 30));
      this.setMaximumSize(new Dimension(30, 30));
      
      setBorder(new LineBorder(Color.cyan.darker(), 1));   // Set cell's border
      
      cellMouseListener = new ClickListener();
      //addMouseListener(new ClickListener());       // Register listener
    }
    
    /**
     * Enable mouse events for this cell.
     */
	public void enableCellMouseEvents(){
			this.addMouseListener(cellMouseListener);
	}
	
	/**
	 * disable mouse events for this cell.
	 */
	public void disableCellMouseEvents(){
			this.removeMouseListener(cellMouseListener);
	}
    
    /**
     * Gets the row of cell.
     *
     * @return the row number of the cell.
     */
    public int getRow() {
		return row;
	}

	/**
	 * Gets the column of this cell.
	 *
	 * @return the column number of the cell
	 */
	public int getColumn() {
		return column;
	}
    
	
	/**
	 * set cell background color.
	 */
	public void highLight(){
    	isMouseOn=1;
    	repaint();
    }

	/**
	 * reset cell background color.
	 */
    public void resetHighlight(){
    	isMouseOn=0;
    	shipHit=0;
    	repaint();
    }

	/**
	 * repaint cell
	 */
    protected void paintComponent(Graphics g) {

      super.paintComponent(g);

      //Change Color of cell if mouse over and exit
      if(shipHit > 0){
    	  if(shipHit==1){
    	      g.setColor(Color.DARK_GRAY);
    	      g.fillRect(0, 0, getWidth(), getHeight());
          }else if(shipHit==2){
              g.setColor(Color.red.brighter());
              g.fillRect(0, 0, getWidth(), getHeight());    	  
          }
      }else if(isMouseOn==1){
	      g.setColor(Color.ORANGE);
	      g.fillRect(0, 0, getWidth(), getHeight());
      }else if(isMouseOn==0){
          g.setColor(Color.lightGray);
          g.fillRect(0, 0, getWidth(), getHeight());    	  
      }

    }
    
	/**
	 * set flag if ship is hit
	 */
    public void setShipHit(){
    	shipHit = 2;
    	repaint();
    }
    
	/**
	 * set flag if hit is missed
	 */
    public void setMissedHit(){
    	shipHit = 1;
    	repaint();    	
    }
    
	/**
	 * Mouse handler class
	 */
	private class ClickListener extends MouseAdapter {

    	public void mouseClicked(MouseEvent e) {
	    	//System.out.println(row + " : " + column);
	    	if(shipHit > 0)return;
	    	shipHit = 1;
	    	gridBoard.onMouseHitAtCell(row,column);	    	
	    	repaint();
	    }

    	public void mouseEntered(MouseEvent e) {
	    	isMouseOn = 1;
	    	repaint();
	    }
	    
    	public void mouseExited(MouseEvent e) {
	    	isMouseOn = 0;
	    	repaint();
	    }
	    
	 }
}
