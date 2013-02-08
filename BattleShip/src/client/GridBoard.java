package client;

import java.awt.*;
import javax.swing.JPanel;



/**
 * The Class GridBoard.
 */
public class GridBoard extends JPanel{
	
	/** The parent game board. */
	private GameBoard gameBoard;
	
	/** The array of cells. */
	private Cell[][] cells =  new Cell[10][10];
	
	/** The array of ship position and hit flags. */
	private int[][][] flags = new int[10][10][2];
	  
	/** The transparent layer. */
	Rectangle rect;
	
	/** The cell width. */
	int cellWidth;
	
	/** The cell height. */
	int cellHeight;
	  
	/**
  	 * Instantiates a new grid board.
  	 *
  	 * @param gb parent game board
  	 */
  	public GridBoard(GameBoard gb){
		
		this.gameBoard = gb;
		  
	    setLayout(new GridLayout(10, 10, 0, 0));

	    for (int i = 0; i < 10; i++)
	      for (int j = 0; j < 10; j++){
	        add(cells[i][j] = new Cell(i, j, this));
	        
	        flags[i][j][0] = 0; //isShip
	        flags[i][j][1] = 0; //isHit
	        
	      }
		this.setPreferredSize(new Dimension(300,300));
		
//		this.setMinimumSize(new Dimension(300,300));
//		this.setMaximumSize(new Dimension(300,300));

	  }

	/**
	 * Reset grid board.
	 */
	public void resetGridBoard(){
		for (int i = 0; i < 10; i++)
		      for (int j = 0; j < 10; j++){		        
		        flags[i][j][0] = 0; //isShip
		        flags[i][j][1] = 0; //isHit
		      }
	}
	  
	/**
	 * Enable mouse events.
	 */
	public void enableMouseEvents(){
		 for (int i = 0; i < 10; i++)
		      for (int j = 0; j < 10; j++)
		        cells[i][j].enableCellMouseEvents();
	}
	
	/**
	 * Disable mouse events.
	 */
	public void disableMouseEvents(){
		 for (int i = 0; i < 10; i++)
		      for (int j = 0; j < 10; j++)
		    	  cells[i][j].disableCellMouseEvents();
	}

	/**
	 * Sets the ship occupied cells.
	 *
	 * @param s the ship for which to set cells.
	 */
	public void setShipOccupiedCellFlag(Ship s){
		//set ship flags
		
		int rowStart = s.getRow(),colStart = s.getColumn();
		int rowEnd =0,colEnd = 0;
		
		if(s.isHorizontal()){
			rowEnd = rowStart;
			colEnd = colStart + s.getLength() - 1;
			
		}else{
			colEnd = colStart;
			rowEnd = rowStart + s.getLength() - 1;
		}
		
		for(int r=rowStart;r<=rowEnd;r++){
			for(int c=colStart;c<=colEnd;c++){
				flags[r][c][0] = s.getId();
			}
		}
		
//		for(int r=0;r<=9;r++){
//			for(int c=0;c<=9;c++){
//				System.out.print(flags[r][c][0] + " ");
//			}
//			System.out.print("\n");
//		}System.out.print("\n");
		
	}
	
	/**
	 * Clear ship cell.
	 *
	 * @param s the ship object for which to clear cells.
	 */
	public void clearShipCell(Ship s){
		
		int rowStart = s.getRow(),colStart = s.getColumn();
		int rowEnd =0,colEnd = 0;
		
		if(s.isHorizontal()){
			rowEnd = rowStart;
			colEnd = colStart + s.getLength() - 1;
			
		}else{
			colEnd = colStart;
			rowEnd = rowStart + s.getLength() - 1;
		}
		
		for(int r=rowStart;r<=rowEnd;r++){
			for(int c=colStart;c<=colEnd;c++){
				flags[r][c][0] = 0;
			}
		}
		
	}
	
	/**
	 * Checks if is ships collide.
	 *
	 * @param point location of cell
	 * @param s the ship object
	 * @return true, if is ships collide at specified location.
	 */
	public boolean isShipCollide(Point point,Ship s){
		int sLength = s.getLength();
		int rowStart, colStart, rowEnd, colEnd;
		
		Cell startCell = (Cell) this.getComponentAt(point);
		rowStart = startCell.getRow();
		colStart = startCell.getColumn();
		rowEnd = startCell.getRow();
		colEnd = startCell.getColumn();

		if(s.isHorizontal()){
			
			if(colStart > 10 - sLength) colStart = 10 - sLength; 			
			colEnd = colStart + sLength - 1;			
			if(colEnd > 10) colEnd = 10;
			
		}else{
			
			if(rowStart > 10 - sLength) rowStart = 10 - sLength; 
			rowEnd = rowStart + sLength - 1;
			if(rowEnd > 10)rowEnd = 10;

		}		
		
		for(int r=rowStart;r <= rowEnd;r++){
			for(int c=colStart;c <= colEnd;c++){
				if(flags[r][c][0] != 0 && flags[r][c][0] != s.getId()){
					//System.out.println("Collide");
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Reset cell hover color.
	 */
	public void resetCellHighlight(){
	    for (int i = 0; i < 10; i++)
		      for (int j = 0; j < 10; j++)
		        cells[i][j].resetHighlight();
	}
	
	/**
	 * Match cells at a location for specified ship.
	 *
	 * @param point cell location.
	 * @param s the ship object.
	 * @param safeCell the previous safe position.
	 * @return the point calculated safe position for the ship.
	 */
	public Point matchShipCellAtPoint(Point point,Ship s,Point safeCell) {
		int sLength = s.getLength();
		int rowStart, colStart, rowEnd, colEnd;
		
		//Cell[] matchCells = new Cell[sLength];
		
		Cell startCell = (Cell) this.getComponentAt(point);
		rowStart = startCell.getRow();
		colStart = startCell.getColumn();
		rowEnd = startCell.getRow();
		colEnd = startCell.getColumn();
		
		resetCellHighlight();
		
		if(s.isHorizontal()){
			
			if(colStart > 10 - sLength) colStart = 10 - sLength; 			
			colEnd = colStart + sLength;			
			if(colEnd > 10) colEnd = 10;
			
			for(int c=colStart;c < colEnd;c++){
				//if collision set safeCell
				if(flags[rowStart][c][0] != 0 && flags[rowStart][c][0] != s.getId()){
					rowStart = safeCell.x;
					colStart = safeCell.y;
					
					if(colStart > 10 - sLength) colStart = 10 - sLength; 			
					colEnd = colStart + sLength;			
					if(colEnd > 10) colEnd = 10;
					
					break;
				}
									
				//cell[rowStart][c].highLight();
			}
			
			for(int c=colStart;c < colEnd;c++){				
				cells[rowStart][c].highLight();
			}

		}else{
			
			if(rowStart > 10 - sLength) rowStart = 10 - sLength; 
			rowEnd = rowStart + sLength;
			if(rowEnd > 10)rowEnd = 10;
			
			for(int r = rowStart;r<rowEnd;r++)
			{
				//if collision set safeCell
				if(flags[r][colStart][0] != 0 && flags[r][colStart][0] != s.getId()){
					rowStart = safeCell.x;
					colStart = safeCell.y;
					
					if(rowStart > 10 - sLength) rowStart = 10 - sLength; 
					rowEnd = rowStart + sLength;
					if(rowEnd > 10)rowEnd = 10;
					
					break;
				}
				//cell[r][colStart].highLight();
			}
			
			for(int r = rowStart;r<rowEnd;r++)
			{
				cells[r][colStart].highLight();
			}
		}
		
//		for(int r=rowStart;r<rowEnd;r++){
//			for(int c=colStart;c<colEnd;c++){
//				cell[r][c].highLight();
//			}
//		}		
		
		return new Point(rowStart,colStart);
	}

	
	/**
	 * Sets the opponent ship position.
	 *
	 * @param flags2 the array of opponent's ship position
	 */
	public void setOpponentShipPosition(int[][][] flags2) {
	    for (int i = 0; i < 10; i++){
		      for (int j = 0; j < 10; j++){
		        flags[i][j][0] = flags2[i][j][0]; //isShip
		        flags[i][j][1] = flags2[i][j][0]; //isHit
		        System.out.print(flags[i][j][0]);
		      }
		      System.out.print("\n");
	    }
	}

	/**
	 * Gets the player's ship position.
	 *
	 * @return the player's ship position
	 */
	public int[][][] getMyShipPosition(){
		return flags;
	}
	
	/**
	 * Sets the hit at specified cell.
	 *
	 * @param row the row of the cell.
	 * @param column the column of cell.
	 * @return the id of the ship if hit.
	 */
	public int setHitAtCell(int row,int column){
        
		flags[row][column][1] = 1; //isHit
		
		if(flags[row][column][0] > 0){
		        System.out.println("Ship id hit:" + flags[row][column][0]);	
				cells[row][column].setShipHit();
				gameBoard.setShipHitById(flags[row][column][0]);
		}
		cells[row][column].setMissedHit();
		return flags[row][column][0];
	}
	
	/**
	 * Player's mouse action on cell.
	 *
	 * @param row the row of cell.
	 * @param column the column of cell.
	 */
	public void onMouseHitAtCell(int row,int column){
        
		flags[row][column][1] = 1; //isHit
		
		if(flags[row][column][0] > 0){
		        System.out.println("Ship id hit:" + flags[row][column][0]);	

				cells[row][column].setShipHit();
				//gameBoard.setShipHitById(flags[row][column][0]);
				gameBoard.onMouseHitAtCell(row, column, true, gameBoard.setShipHitById(flags[row][column][0]));
				return;
		}
		cells[row][column].setMissedHit();
		gameBoard.onMouseHitAtCell(row, column, false, false);
		
	}
	  
}
