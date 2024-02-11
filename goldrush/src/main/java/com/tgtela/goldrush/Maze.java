package com.tgtela.goldrush;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import javafx.util.*;
/**
 * @author Tenho Laakkio.
 * Code for the Monad 2024 Rekry challenge
 */


/**
 * Represents a maze grid for navigation and pathfinding.
 */

public class Maze{
	private int rows;
	private int cols;
	private Coord[][] grid;
	private Coord currentPosition;
	private int currentRotation;
	ArrayList<Coord> shortestPathToTarget=new ArrayList<Coord>();
	private boolean backTrack=false;
	private Coord target;
	
	
	 /**
     * Constructs a Maze object with the specified number of rows and columns.
     * 
     * @param rows The number of rows in the maze grid.
     * @param cols The number of columns in the maze grid.
     */
	public Maze(int rows,int cols) {
		this.rows=rows;
		this.cols=cols;
		initializeMaze();
	}
	public Coord[][] getGrid(){
		return grid;
	}
	 /**
     * Initializes the maze grid with empty coordinates.
     * 
     * @return The initialized maze grid.
     */
    private Coord[][] initializeMaze() {
    	grid=new Coord[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                grid[i][j] = new Coord(j,i);
            }
        }
        return grid;
    }
    public Coord getCoord(int y,int x) {
    	return grid[y][x];
    }
    public void setTarget(int y,int x) {
    	target=grid[y][x];
    }
    public Coord getTarget() {
    	return target;
    }
    public Coord getCurrentPosition() {
		return currentPosition;
	}
	public void setCurrentPosition(Coord currentPosition) {
		this.currentPosition = currentPosition;
	}
	public int getCurrentRotation() {
		return currentRotation;
	}
	public void setCurrentRotation(int currentRotation) {
		this.currentRotation = currentRotation;
	}
	public boolean isBackTrack() {
		return backTrack;
	}
	public void setBackTrack(boolean backTrack) {
		this.backTrack = backTrack;
	}

	public ArrayList<Coord> getPathToTarget(){
		return shortestPathToTarget;
	}
	public void setPathToTarget(ArrayList<Coord> pathToTarget) {
		this.shortestPathToTarget=pathToTarget;
	}
	public void removeFromPathToTarget() {
		this.shortestPathToTarget.remove(0);
	}
	
	public int getCols() {
		return cols;
	}
	public int getRows() {
		return rows;
	}
    


    /**
     * Determines which walls are not present according to the square value
     * @param square The square value.
     * @return A list of angles (in degrees) indicating which walls are not present.
     */
	   public ArrayList<Integer> whichNotWalls(int square){
	    	ArrayList<Integer> walls=new ArrayList<Integer>(Arrays.asList(0,90,180,270));
	    	switch (square) {
	    	// 1 2 4 8
	    	// north east south west
	    	//0000
	        case 0:
	            return walls;
	        //0001
	        case 1:
	            walls.remove(Integer.valueOf(270));
	            return walls;
	        //0010
	        case 2:
	        	walls.remove(Integer.valueOf(180));
	        	return walls;
	        //0011
	        case 3:
	        	walls.remove(Integer.valueOf(180));
	        	walls.remove(Integer.valueOf(270));
	        	return walls;
	        //0100
	        case 4:
	        	walls.remove(Integer.valueOf(90));
	        	return walls;
	        //0101
	        case 5:
	        	walls.remove(Integer.valueOf(90));
	        	walls.remove(Integer.valueOf(270));
	        	return walls;
	        //0110
	        case 6:
	        	walls.remove(Integer.valueOf(90));
	        	walls.remove(Integer.valueOf(180));
	        	return walls;
	       //0111
	        case 7:
	        	walls.remove(Integer.valueOf(90));
	        	walls.remove(Integer.valueOf(180));
	        	walls.remove(Integer.valueOf(270));
	        	return walls;
	        //1000
	        case 8:
	        	walls.remove(Integer.valueOf(0));
	            return walls;
	        //1001
	        case 9:
	        	walls.remove(Integer.valueOf(0));
	        	walls.remove(Integer.valueOf(270));
	        	return walls;
	        //1010
	        case 10:
	        	walls.remove(Integer.valueOf(0));
	        	walls.remove(Integer.valueOf(180));
	            return walls;
	        //1011
	        case 11:
	        	walls.remove(Integer.valueOf(0));
	        	walls.remove(Integer.valueOf(180));
	        	walls.remove(Integer.valueOf(270));
	        	return walls;
	       //1100
	        case 12:
	        	walls.remove(Integer.valueOf(0));
	        	walls.remove(Integer.valueOf(90));
	            return walls;
	         //1101
	        case 13:
	        	walls.remove(Integer.valueOf(0));
	        	walls.remove(Integer.valueOf(90));
	        	walls.remove(Integer.valueOf(270));
	            return walls;
	        //1110
	        case 14:
	        	walls.remove(Integer.valueOf(0));
	        	walls.remove(Integer.valueOf(90));
	        	walls.remove(Integer.valueOf(180));
	        	return walls;
	        //1111
	        case 15:
	        	walls.remove(Integer.valueOf(0));
	        	walls.remove(Integer.valueOf(90));
	        	walls.remove(Integer.valueOf(180));
	        	walls.remove(Integer.valueOf(270));
	        	walls.add(Integer.valueOf(-1));
	        	return walls;
	        default:
	            return walls;
	    }
	    }
	   public boolean isDiscoveredUnvisited() {
		   boolean value=false;
		   for (int i = 0; i < rows; i++) {
	            for (int j = 0; j < cols; j++) {
	            	if (canGetToCoord(i,j,grid[i][j].getSquare()).contains(target) && canGetToCoord(i,j,grid[i][j].getSquare()).size()==1) {
	            		grid[i][j].setVisited(true);
	            		grid[i][j].setDiscovered(true);
	            	
	            	}
	                if (grid[i][j].isDiscovered() && !grid[i][j].isVisited() && grid[i][j]!=target) {
	                	value=true;
	                }
	            }
	        }
		   return value;
		   
	   }
	   /**
	     * Returns a list of possible coordinates that can be reached from the specified position.
	     * 
	     * @param row    The row index of the current position.
	     * @param col    The column index of the current position.
	     * @param square The square configuration of the current position.
	     * @return A list of coordinates that can be reached without rotation.
	     */
    public List<Coord> canGetToCoord(int row,int col,int square){
		
		List<Integer> notWalls=whichNotWalls(square);
		
		
		List<Coord> canGetTo=new ArrayList<Coord>();
		for (Integer notWall:notWalls) {
			
			if (notWall==0) {
				if (row>0) {
				
				
				canGetTo.add(grid[row-1][col]);
				}
			}
			else if (notWall==90) {
				if (col<cols-1) {
				
				
				canGetTo.add(grid[row][col+1]);
				}
			}
			else if (notWall==180) {
				if (row<rows-1) {
				
				
				canGetTo.add(grid[row+1][col]);
				}
			}
			else if (notWall==270) {
				if (col>0) {
				
				
				canGetTo.add(grid[row][col-1]);
				}
			}
		}
		return canGetTo;
	}

    /**
     * Returns a list of possible coordinates with their associated rotations that can be reached from the specified position.
     * 
     * @param row    The row index of the current position.
     * @param col    The column index of the current position.
     * @param square The square value, which represents the walls that are around the current position
     * @return A list of pairs containing the rotation angle and the corresponding coordinate.
     */
    public List<Pair<Integer,Coord>> canGetToCoordWithRotation(int row,int col,int square){
		
		List<Integer> notWalls=whichNotWalls(square);
		
		
		
		List<Pair<Integer,Coord>> canGetTo=new ArrayList<Pair<Integer,Coord>>();
		for (Integer notWall:notWalls) {
			
			if (notWall==0) {
				if (row>0) {
				
				canGetTo.add(new Pair<Integer,Coord>(notWall, grid[row-1][col]));
				}
			}
			else if (notWall==90) {
				if (col<cols-1) {
				canGetTo.add(new Pair<Integer,Coord>(notWall, grid[row][col+1]));
				}
			}
			else if (notWall==180) {
				if (row<rows-1) {

				canGetTo.add(new Pair<Integer,Coord>(notWall, grid[row+1][col]));
				}
			}
			else if (notWall==270) {
				if (col>0) {

				canGetTo.add(new Pair<Integer,Coord>(notWall, grid[row][col-1]));
				}
			}
		}
		return canGetTo;
	}
    

    /**
     * Returns a list of possible coordinates with their associated rotations that can be reached from the specified position,
     * considering the target position for pathfinding.
     * 
     * @param row    The row index of the current position.
     * @param col    The column index of the current position.
     * @param square The square configuration of the current position.
     * @return A list of pairs containing the rotation angle and the corresponding coordinate.
     */
    public List<Pair<Integer,Coord>> canGetToCoordWithRotationTarget(int row,int col,int square){
		
		List<Integer> notWalls=whichNotWalls(square);
		
		
		
		List<Pair<Integer,Coord>> canGetTo=new ArrayList<Pair<Integer,Coord>>();
		for (Integer notWall:notWalls) {
			
			if (notWall==0) {
				if (row>0) {
				
				canGetTo.add(new Pair<Integer,Coord>(0, grid[row-1][col]));
				}
			}
			else if (notWall==90) {
				if (col<cols-1) {
				
				canGetTo.add(new Pair<Integer,Coord>(90, grid[row][col+1]));
				
				}
			}
			else if (notWall==180) {
				if (row<rows-1) {
				
				
				canGetTo.add(new Pair<Integer,Coord>(180, grid[row+1][col]));
				}
			}
			else if (notWall==270) {
				if (col>0) {
				
				
				canGetTo.add(new Pair<Integer,Coord>(270, grid[row][col-1]));
				}
			}
		}
		if (row<rows-1 && col<cols-1 && grid[row+1][col+1].isVisited() && (notWalls.contains(90) || notWalls.contains(180)) 
				&& (whichNotWalls(grid[row+1][col+1].getSquare()).contains(0) || whichNotWalls(grid[row+1][col+1].getSquare()).contains(270))) {
			if ((notWalls.contains(180) || whichNotWalls(grid[row+1][col+1].getSquare()).contains(0)) && 
					(notWalls.contains(90) || whichNotWalls(grid[row+1][col+1].getSquare()).contains(270))) {
				canGetTo.add(new Pair<Integer,Coord>(135,grid[row+1][col+1]));
				
			}
			
		}
		
		if (col>0 && row>0 && grid[row-1][col-1].isVisited() && (notWalls.contains(270) || notWalls.contains(0)) 
				&& (whichNotWalls(grid[row-1][col-1].getSquare()).contains(90) || whichNotWalls(grid[row-1][col-1].getSquare()).contains(180))) {
			if ((notWalls.contains(0) || whichNotWalls(grid[row-1][col-1].getSquare()).contains(180)) && 
					(notWalls.contains(270) || whichNotWalls(grid[row-1][col-1].getSquare()).contains(90))) {
				canGetTo.add(new Pair<Integer,Coord>(315,grid[row-1][col-1]));
				
				
			}
			
		}
		
		if (col<cols-1 && row>0 && grid[row-1][col+1].isVisited() &&(notWalls.contains(90) || notWalls.contains(0)) 
				&& (whichNotWalls(grid[row-1][col+1].getSquare()).contains(270) || whichNotWalls(grid[row-1][col+1].getSquare()).contains(180))) {
			if ((notWalls.contains(0) || whichNotWalls(grid[row-1][col+1].getSquare()).contains(180)) && 
					(notWalls.contains(90) || whichNotWalls(grid[row-1][col+1].getSquare()).contains(270))) {
				canGetTo.add(new Pair<Integer,Coord>(45,grid[row-1][col+1]));
				
				
			}
			
		}
		if (row<rows-1 && col>0 && grid[row+1][col-1].isVisited() && (notWalls.contains(270) || notWalls.contains(180)) 
				&& (whichNotWalls(grid[row+1][col-1].getSquare()).contains(90) || whichNotWalls(grid[row+1][col-1].getSquare()).contains(0))) {
			if ((notWalls.contains(180) || whichNotWalls(grid[row+1][col-1].getSquare()).contains(0)) && 
					(notWalls.contains(270) || whichNotWalls(grid[row+1][col-1].getSquare()).contains(90))) {
				canGetTo.add(new Pair<Integer,Coord>(225,grid[row+1][col-1]));
				
				
			}
			
		}
		
		return canGetTo;
	}
    
    

  
	
}

class Coord{
	private int x;
	private int y;
	private int timesVisited;
	private int square;
	private boolean visited;
	private boolean discovered;
	public Coord(int x,int y) {
		this.visited=false;
		this.setDiscovered(false);
		this.x=x;
		this.y=y;
		this.timesVisited=0;
	}
	public int getTimesVisited() {
		return timesVisited;
	}
	public void setTimesVisited(int timesVisited) {
		this.timesVisited = timesVisited;
	}
	public void addTimesVisited(int timesVisited) {
		this.timesVisited=timesVisited;
	}
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	public boolean isVisited() {
		return visited;
	}
	public void setVisited(boolean visited) {
		this.visited = visited;
		this.setDiscovered(visited);
	
	}

		
		public int getSquare() {
			return square;
		}
		public void setSquare(int square) {
			this.square = square;
		}
		public boolean isDiscovered() {
			return discovered;
		}
		public void setDiscovered(boolean discovered) {
			this.discovered = discovered;
		}
		
		
}




	

   
/**
 * Represents the updated state of the game
 * that can be get from the websocket
 * 
 */
class GameState {
	private int moves;
	private int timer;
	private Coord start;
	private int startRotation;
	private Coord target;
	private int square;
	private int rows;
	private int columns;
	private Coord current;
	private int currentRotation;
	
	public GameState() {
		
		
		
	}
	

	public int getMoves() {
		return moves;
	}
	public void setMoves(int moves) {
		this.moves = moves;
	}
	public int getTimer() {
		return timer;
	}
	public void setTimer(int timer) {
		this.timer = timer;
	}
	public Coord getStart() {
		return start;
	}
	public void setStart(Coord start) {
		this.start = start;
	}
	public int getStartRotation() {
		return startRotation;
	}
	public void setStartRotation(int startRotation) {
		this.startRotation = startRotation;
	}
	public Coord getTarget() {
		return target;
	}
	public void setTarget(Coord target) {
		this.target = target;
	}
	public int getSquare() {
		return square;
	}
	public void setSquare(int square) {
		this.square = square;
	}
	public int getRows() {
		return rows;
	}
	public void setRows(int rows) {
		this.rows = rows;
	}
	public int getColumns() {
		return columns;
	}
	public void setColumns(int columns) {
		this.columns = columns;
	}

	public Coord getCurrent() {
		return current;
	}

	public void setCurrent(Coord current) {
		this.current = current;
	}

	public int getCurrentRotation() {
		return currentRotation;
	}

	public void setCurrentRotation(int currentRotation) {
		this.currentRotation = currentRotation;
	}


	
	
	
	

	



	
   
}
