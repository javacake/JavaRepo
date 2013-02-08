package client;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.*;
import java.awt.Graphics2D;



/**
 * The Class GameBoard. Made of grid of cells and ship panels.
 */
public class GameBoard extends JLayeredPane{
	
    /** The Constant WIDTH of the board. */
    public static final int WIDTH = 300;
    
    /** The Constant HEIGHT of the board */
    public static final int HEIGHT = 300;
    
    /** The Constant board size. */
    private static final Dimension PANE_SIZE = new Dimension(WIDTH, HEIGHT);
    
    /** The parent window frame. */
    private ClientFrame cframe;
    
	/** The cell grid board. */
	private GridBoard gridBoard;
	
	/** The ship layout board. */
	private ShipBoard shipBoard;
	
	/** The transparent layer flag. */
	private boolean topRect;
	
	/** The ship destroyed counter. */
	private int shipDestroyed;
	
	/** The my mouse adapter. */
	MyMouseAdapter myMouseAdapter;

	/**
	 * Instantiates a new game board.
	 *
	 * @param cf the parent window.
	 */
	public GameBoard(ClientFrame cf){
		cframe = cf;
		
		//GridBoard
			gridBoard = new GridBoard(this);
			gridBoard.setSize(PANE_SIZE);
			//gridBoard.setBackground(Color.blue.darker());
		
		//ShipBoard
			shipBoard = new ShipBoard(this);
			shipBoard.setSize(PANE_SIZE);
			shipBoard.setOpaque(false);

		setSize(PANE_SIZE);
        add(gridBoard, JLayeredPane.DEFAULT_LAYER);
        add(shipBoard,JLayeredPane.PALETTE_LAYER);
        
        myMouseAdapter = new MyMouseAdapter(this);
        //addMouseListener(myMouseAdapter);
        //addMouseMotionListener(myMouseAdapter);
		
	}
	
	
	/**
	 * Gets the ship bounds.
	 *
	 * @param s ship object
	 * @return calculated ship bounds
	 */
	public Rectangle getShipBounds(Ship s) {
		//return position and width, height of ship lable to be draw.
		
		int width = 0,height = 0;
		Point cord = new Point(30 * s.getColumn(),30 * s.getRow());
		
		if(s.isHorizontal()){
			height = 30; 
			width = 30 * s.getLength();
		}
		else{
			height = 30 * s.getLength();
			width = 30;
		}
		
		//set Flags after ship has fixed position and ready to draw over these cells
		gridBoard.setShipOccupiedCellFlag(s); //set ship grid flags
		
		return new Rectangle(cord.x + 2, cord.y + 2, width-4, height-4);
	}
	
	
	/**
	 * The Class to handle Mouse events.
	 */
	private class MyMouseAdapter extends MouseAdapter {
		
		/** The gbm. */
		private GameBoard gbm;
        
        /** The drag label width div2. */
        private int dragLabelWidthDiv2;
        
        /** The drag label height div2. */
        private int dragLabelHeightDiv2;
        
        /** The clicked ship. */
        private Ship clickedShip = null;
        
        /** The drop cell. */
        private Point dropCell;
        
        /** The safe cell. */
        private Point safeCell;
        
        /**
         * Instantiates a new my mouse adapter.
         *
         * @param gb the gb
         */
        public MyMouseAdapter(GameBoard gb){
        	gbm = gb;
        	safeCell = new Point();
        }

        /* (non-Javadoc)
         * @see java.awt.event.MouseAdapter#mouseClicked(java.awt.event.MouseEvent)
         */
        public void mouseClicked(MouseEvent me) {
        	if(shipBoard.getComponentAt(me.getPoint()) instanceof Ship)
        	{
	        	clickedShip = (Ship) shipBoard.getComponentAt(me.getPoint());
	        	safeCell.setLocation(clickedShip.getRow(),clickedShip.getColumn());
	        	//safeCell.x = clickedShip.getRow();
	        	//safeCell.y = clickedShip.getColumn();
	        	
	        	gridBoard.clearShipCell(clickedShip);
	        	
	        	clickedShip.setHorizontal(!clickedShip.isHorizontal());
	        	//if ship collide then dont change orientation
	        	if(gridBoard.isShipCollide(me.getPoint(), clickedShip)) clickedShip.setHorizontal(!clickedShip.isHorizontal());
                
	        	dropCell = gbm.gridBoard.matchShipCellAtPoint(me.getPoint(),clickedShip,safeCell);
                
                clickedShip.setRow(dropCell.x);
                clickedShip.setColumn(dropCell.y);
                
                //gridBoard.setShipCell(clickedShip);
                
                clickedShip.setBounds(getShipBounds(clickedShip));
                gridBoard.resetCellHighlight();
        	}
        }
        
        /* (non-Javadoc)
         * @see java.awt.event.MouseAdapter#mousePressed(java.awt.event.MouseEvent)
         */
        public void mousePressed(MouseEvent me) {
        	
        	if(shipBoard.getComponentAt(me.getPoint()) instanceof Ship)
        	{
	        	clickedShip = (Ship) shipBoard.getComponentAt(me.getPoint());
	        	safeCell.setLocation(clickedShip.getRow(),clickedShip.getColumn());
	        	//safeCell.x = clickedShip.getRow();
	        	//safeCell.y = clickedShip.getColumn();

	        	
                dragLabelWidthDiv2 = 15 - 4;
                dragLabelHeightDiv2 = 15- 4;

                int x = me.getPoint().x - dragLabelWidthDiv2;
                int y = me.getPoint().y - dragLabelHeightDiv2;
                clickedShip.setLocation(x, y);

                shipBoard.repaint();
                
                dropCell = gbm.gridBoard.matchShipCellAtPoint(me.getPoint(),clickedShip,safeCell);
                
        	}
        }

        /* (non-Javadoc)
         * @see java.awt.event.MouseAdapter#mouseDragged(java.awt.event.MouseEvent)
         */
        public void mouseDragged(MouseEvent me) {
            if (clickedShip == null) {
                return;
            }
            
            if(!clickedShip.getParent().getBounds().contains(me.getPoint())) return;
            
            int x = me.getPoint().x - dragLabelWidthDiv2;
            int y = me.getPoint().y - dragLabelHeightDiv2;
            
            clickedShip.setLocation(x, y);
            
            repaint(); //shipboard repaint
            
            
            dropCell = gbm.gridBoard.matchShipCellAtPoint(me.getPoint(),clickedShip,safeCell);
            safeCell = dropCell;
        }

        /* (non-Javadoc)
         * @see java.awt.event.MouseAdapter#mouseReleased(java.awt.event.MouseEvent)
         */
        public void mouseReleased(MouseEvent me) {
            
        	if (clickedShip == null) {
                return;
            }
    
        	gridBoard.clearShipCell(clickedShip);

            clickedShip.setRow(dropCell.x);
            clickedShip.setColumn(dropCell.y);
            clickedShip.setBounds(getShipBounds(clickedShip));            

            repaint();
            clickedShip = null;
            gridBoard.resetCellHighlight();
        }
    }

	/**
	 * Enable mouse events.
	 *
	 * @param sender Id of the board.
	 */
	public void enableMouseEvents(int sender){
        if(sender == 1){
			this.addMouseListener(myMouseAdapter);
	        this.addMouseMotionListener(myMouseAdapter);	        
        }else{
        	gridBoard.enableMouseEvents();
        }
        
        fade(false);
	}
	
	/**
	 * Disable mouse events.
	 *
	 * @param sender Id of the board.
	 */
	public void disableMouseEvents(int sender){
		if(sender == 1){
			this.removeMouseListener(myMouseAdapter);
	        this.removeMouseMotionListener(myMouseAdapter);
		}else{
			gridBoard.disableMouseEvents();
		}

		fade(true);
	}
	
	/**
	 * Hide all ship.
	 */
	public void hideAllShip(){
		shipBoard.hideAllShip();
	}
	
	
	/**
	 * Sets the opponent ship position.
	 *
	 * @param flags array of the opponent ship position.
	 */
	public void setOpponentShipPosition(int[][][] flags){
		gridBoard.setOpponentShipPosition(flags);
	}
	
	/**
	 * Gets the my ship position.
	 *
	 * @return player's ship position.
	 */
	public int[][][] getMyShipPosition(){
		return gridBoard.getMyShipPosition();
	}
	
	/**
	 * Sets the opponent ship location.
	 *
	 * @param shipLocation the opponent's ship layout.
	 */
	public void setOpponentShipLocation(int[][] shipLocation){
		shipBoard.setOpponentShipLocation(shipLocation);
		repaint();
	}
	
	/**
	 * Gets the my ship location.
	 *
	 * @return the player's layout.
	 */
	public int[][] getMyShipLocation(){
		return shipBoard.getMyShipLocation();
	}
	
	/**
	 * Assign the ship hit of specified id.
	 *
	 * @param shipId id of the ship.
	 * @return true, if ship destroyed.
	 */
	public boolean setShipHitById(int shipId){
		if(shipBoard.setShipHitById(shipId)){
			System.out.println("Ship id:" + shipId + " Destroyed.");
			shipDestroyed++;
//			if(shipDestroyed == 5){
//				cframe.gameOverStatus(true);
//				System.out.println("Player Won!!");
//			}
			shipBoard.showShip(shipId);
			return true;
		}
		return false;
	}

	/**
	 * Sets opponent's mouse hit at specified cell position.
	 *
	 * @param row the row of the cell
	 * @param column the column of the cell
	 */
	public void setHitAtCell(int row,int column){
		gridBoard.setHitAtCell(row, column);
	}

	/**
	 * Sets player's mouse hit at cell position.
	 *
	 * @param row the row of the cell.
	 * @param column the column of the cell.
	 * @param hit if any ship hit.
	 * @param sdst if any ship destroyed. 
	 */
	public void onMouseHitAtCell(int row, int column, boolean hit, boolean sdst){
		cframe.opponentMouseHit(row,column,hit,sdst);
	}
	
 	/**
	  * Toggle transparent layer on game board.  
	  *
	  * @param f whether to display or not.
	  */
	 public void fade(boolean f){
		topRect = f;
		repaint();
	}
	
	public void paint(Graphics g) {
		super.paint(g);
		
		if(topRect){
			Graphics2D g2d = (Graphics2D)g;
	        g2d.setColor(Color.WHITE); 
	        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f));
	        g2d.fillRect(0,0,this.getWidth(),this.getHeight());
		}
	}

	
	/**
	 * Reset current board.
	 */
	public void resetGameBoard() {
		gridBoard.resetGridBoard();
		gridBoard.resetCellHighlight();
		shipBoard.resetShipBoard();
		shipDestroyed = 0;
	}
	
	/**
	 * Gets the ship destroyed in current game board.
	 *
	 * @return number of ship destroyed.
	 */
	public int getShipDestroyed(){
		return shipDestroyed;
	}
	

}
