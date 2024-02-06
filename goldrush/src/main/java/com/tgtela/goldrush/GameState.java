package com.tgtela.goldrush;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

import javafx.util.*;

class Coord{
	private int x;
	private int y;
	private int timesVisited;
	private boolean visited;
	private List<Integer> neighbours;
	public Coord(int x,int y) {
		this.neighbours=Collections.EMPTY_LIST;
		this.visited=false;
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
	}
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
	    public Coord returnLocationWithCoord(Integer x,Integer y) {
	    	if (this.x==x && this.y==y) {
	    		return this;
	    	}
	    	return null;
	    }
		public List<Integer> getNeighbours() {
			return neighbours;
		}

		public void setNeighbours(List<Integer> neighbours) {
			this.neighbours = neighbours;
		}
		public void addNeighbour(Integer neighbour) {
			this.neighbours.add(neighbour);
		}
		public void removeNeighbour(Integer neighbour) {
			this.neighbours.remove(Integer.valueOf(neighbour));
		}
}
class Location {
    private int x;
    private int y;
    private List<String> bannedDirections;
    private List<Integer> neighbours;
    private int StraightDirection;
    private int leftDirection;
    private int RightDirection;
    private int backDirection;
    
    

    public Location(Coord coord) {
        this.x = coord.getX();
        this.y = coord.getY();
        
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
    public void addBannedDirection(String direction){
    	if (!bannedDirections.contains(direction)) {
    		this.bannedDirections.add(direction);
    	}
    }
    public List<String> getBannedDirections() {
    	return bannedDirections;
    }
 
    
    
    public ArrayList<Coord> possibleLocations(Coord currentLocation,int square,int currentRotation){
    	 ArrayList<Coord> possibleLocations = new ArrayList<>();

         switch (currentRotation) {
             case 0:
                 // Check the walls of the current square
                 if ((square & 1) == 0) {
                     possibleLocations.add(new Coord(x, y - 1)); // Move up
                 }
                 if ((square & 2) == 0) {
                     possibleLocations.add(new Coord(x + 1, y)); // Move right
                 }
                 if ((square & 4) == 0) {
                     possibleLocations.add(new Coord(x, y + 1)); // Move down
                 }
                 if ((square & 8) == 0) {
                     possibleLocations.add(new Coord(x - 1, y)); // Move left
                 }
                 break;

             case 90:
                 // Adjust for the new rotation (90 degrees clockwise)
                 if ((square & 8) == 0) {
                     possibleLocations.add(new Coord(x, y - 1)); // Move up
                 }
                 if ((square & 1) == 0) {
                     possibleLocations.add(new Coord(x + 1, y)); // Move right
                 }
                 if ((square & 2) == 0) {
                     possibleLocations.add(new Coord(x, y + 1)); // Move down
                 }
                 if ((square & 4) == 0) {
                     possibleLocations.add(new Coord(x - 1, y)); // Move left
                 }
                 break;

             case 180:
                 // Adjust for the new rotation (180 degrees clockwise)
                 if ((square & 4) == 0) {
                     possibleLocations.add(new Coord(x, y - 1)); // Move up
                 }
                 if ((square & 8) == 0) {
                     possibleLocations.add(new Coord(x + 1, y)); // Move right
                 }
                 if ((square & 1) == 0) {
                     possibleLocations.add(new Coord(x, y + 1)); // Move down
                 }
                 if ((square & 2) == 0) {
                     possibleLocations.add(new Coord(x - 1, y)); // Move left
                 }
                 break;

             case 270:
                 // Adjust for the new rotation (270 degrees clockwise)
                 if ((square & 2) == 0) {
                     possibleLocations.add(new Coord(x, y - 1)); // Move up
                 }
                 if ((square & 4) == 0) {
                     possibleLocations.add(new Coord(x + 1, y)); // Move right
                 }
                 if ((square & 8) == 0) {
                     possibleLocations.add(new Coord(x, y + 1)); // Move down
                 }
                 if ((square & 1) == 0) {
                     possibleLocations.add(new Coord(x - 1, y)); // Move left
                 }
                 break;

             default:
                 break;
         }

         return possibleLocations;
     }

    
    
    

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Location location = (Location) obj;
        return x == location.x && y == location.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

	public int getStraightDirection() {
		return StraightDirection;
	}

	public void setStraightDirection(int straightDirection) {
		this.StraightDirection = straightDirection;
	}

	public int getLeftDirection() {
		return leftDirection;
	}

	public void setLeftDirection(int leftDirection) {
		this.leftDirection = leftDirection;
	}

	public int getRightDirection() {
		return RightDirection;
	}

	public void setRightDirection(int rightDirection) {
		this.RightDirection = rightDirection;
	}

	public int getBackDirection() {
		return backDirection;
	}

	public void setBackDirection(int backDirection) {
		this.backDirection = backDirection;
	}
	
	 public static Location calculateAndSetDirections(Location location,int currentRotation) {
	    	location.setStraightDirection(currentRotation);
	    	switch (currentRotation) {
	    	case 0:
	    		location.setRightDirection(90);
	    		location.setLeftDirection(270);
	    		location.setBackDirection(180);
	    	
	    		return location;
	    	case 90:
	    		location.setRightDirection(180);
	    		location.setLeftDirection(0);
	    		location.setBackDirection(270);
	    		return location;
	    		
	    	case 180:
	    		location.setRightDirection(270);
	    		location.setLeftDirection(90);
	    		location.setBackDirection(0);
	    		return location;
	    		
	    	case 270:
	    		location.setRightDirection(0);
	    		location.setLeftDirection(180);
	    		location.setBackDirection(90);
	    		return location;
	    		
	    	default: 
	    		break;
	    	}
	    	return location;
	    	
	    	
	    }






	

   
}
class Player{
	private Coord position;
	private int rotation;
	private ArrayList<Coord> visitedLocations=new ArrayList<Coord>();
	private Map<Pair<Integer,Integer>,Integer> timesVisited=new HashMap<Pair<Integer,Integer>,Integer>();
	public Player(Coord position,int rotation) {
		this.setPosition(position);
		this.setRotation(rotation);
	}
	public Coord getPosition() {
		return position;
	}
	public void setPosition(Coord position) {
		this.position = position;
	}
	public int getRotation() {
		return rotation;
	}
	public void setRotation(int rotation) {
		this.rotation = rotation;
	}
	public ArrayList<Coord> getVisitedLocations(){
		
		return visitedLocations;
	}
	public Coord getLocation(int x,int y) {
		for (Coord loc:visitedLocations) {
			if (loc.getX()==x && loc.getY()==y) {
				return loc;
			}
		}
		return null;
	}
	public boolean isVisited(int x,int y) {
    	boolean value=false;
    	for (Coord pair:visitedLocations) {
    		if (pair.getX()==x && pair.getY()==y) {
    			value=true;
    			
    		}
    	}
    	return value;
    	
    }
	
	   public void removeLocation(Coord coord) {
		   for (Coord loc:visitedLocations) {
			   if (loc.getX()==coord.getX() && loc.getY()==coord.getY()) {
				   visitedLocations.remove(loc.getX());
			   }
			   
		   }
	    }
	    
	    public void addLocation(Integer x,Integer y) {
	    	boolean value=true;
	    	for (Coord pair:visitedLocations) {
	    		if (pair.getX()==x && pair.getY()==y) {
	    			value=false;
	    			
	    		}
	    	}
	    	if (value) {
	    		this.visitedLocations.add(new Coord(x,y));
	    	}
	    	
	    }
		
		private static boolean isOppositeDirection(int rotation1, int rotation2) {
	        return Math.abs(rotation1 - rotation2) == 180;
	    }
		public static Coord findValidNotVisited(Coord currentLocation, ArrayList<Integer> notWalls,Player player) {
			
		    if (!player.isVisited(currentLocation.getX(), currentLocation.getY() +1) && notWalls.contains(180)) {
		        return new Coord(currentLocation.getX(), currentLocation.getY() + 1);
		    } else if (!player.isVisited(currentLocation.getX() + 1, currentLocation.getY()) && notWalls.contains(90)) {
		        return new Coord(currentLocation.getX() + 1, currentLocation.getY());
		    } else if (!player.isVisited(currentLocation.getX() - 1, currentLocation.getY()) && notWalls.contains(270)) {
		        return new Coord(currentLocation.getX() - 1, currentLocation.getY());
		    } else if (!player.isVisited(currentLocation.getX(), currentLocation.getY() - 1) && notWalls.contains(0)) {
		        return new Coord(currentLocation.getX(), currentLocation.getY() - 1);
		    } else {
		    	
		    	
		    	return new Coord(currentLocation.getX(), currentLocation.getY() + 1);
		    }
		}
	    

	
		public List<Coord> getValidLocations(List<Integer> validRotations,Coord currentLocation,Set<Coord> visitedLocations,int currentRotation) {
			List<Coord> validLocations=new ArrayList<Coord>();
			Map<Integer,Boolean> mapOfValidLocation = new HashMap<>();
			mapOfValidLocation.put(0, true);
			mapOfValidLocation.put(90, true);
			mapOfValidLocation.put(180, true);
			mapOfValidLocation.put(270, true);
	        for (Coord location:visitedLocations) {
	        	if (location.getY()==currentLocation.getY()-1) {
	        		mapOfValidLocation.put(0, false);
	        	}
	        	else if (location.getX()==currentLocation.getX()+1) {
	        		mapOfValidLocation.put(90, false);
	        	}
	        	else if (location.getY()==currentLocation.getY()+1) {
	        		mapOfValidLocation.put(180, false);
	        	}
	        	else if (location.getX()==currentLocation.getX()-1) {
	        		mapOfValidLocation.put(270, false);
	        	}
	        	
	        }
	        for (Entry<Integer, Boolean> entry:mapOfValidLocation.entrySet()) {
	        	if (entry.getKey()==0 && entry.getValue() && !isOppositeDirection(90, currentRotation) && 0!=currentRotation) {
	        		validLocations.add(new Coord(currentLocation.getX(),currentLocation.getY()-1));
	        	}
	        	if (entry.getKey()==90 && entry.getValue() && !isOppositeDirection(90, currentRotation) && 90!=currentRotation) {
	        		validLocations.add(new Coord(currentLocation.getX()+1,currentLocation.getY()));
	        		
	        	}
	        	if (entry.getKey()==180 && entry.getValue() && !isOppositeDirection(90, currentRotation) && 180!=currentRotation) {
	        		validLocations.add(new Coord(currentLocation.getX(),currentLocation.getY()+1));
	        		
	        	}
	        	if (entry.getKey()==270 && entry.getValue() && !isOppositeDirection(90, currentRotation) && 270!=currentRotation) {
	        		validLocations.add(new Coord(currentLocation.getX()-1,currentLocation.getY()));
	        	}

	        }
	        

	        return validLocations;
	    }
		public List<Integer> getValidRotations(GameState gameState) {
	        List<Integer> validRotations = new ArrayList<>();
	        int[] rotationValues = {0, 90, 180, 270}; // Possible rotation values

	        Map<Integer, Boolean> walls = getWalls(gameState.getSquare());

	        for (int rotation : rotationValues) {
	            if (!walls.get(rotation)) {
	                validRotations.add(rotation);
	            }
	        }

	        return validRotations;
	    }
		public static Map<Integer, Boolean> getWalls(int square) {
	        int[] masks = {0b1000, 0b0100, 0b0010, 0b0001};
	        Map<Integer, Boolean> walls = new HashMap<>();

	        walls.put(0, (square & masks[0]) != 0);
	        walls.put(90, (square & masks[1]) != 0);
	        walls.put(180, (square & masks[2]) != 0);
	        walls.put(270, (square & masks[3]) != 0);

	        return walls;
	    }
		/*
		public static Location calculateNextPosition(Player player,Location currentLocation,int rotation,GameState gameState) {
			if (rotation==0 || rotation==90) {
			if (currentLocation.getX()==9 || currentLocation.getY()==9){
				return currentLocation;
			}
			}
			else if (rotation==180 || rotation==270) {
				if (currentLocation.getX()==9 || currentLocation.getY()==9){
					return currentLocation;
				}
				}
			
			Map<Integer,Boolean> walls=getWalls(gameState.getSquare());
			switch (rotation) {
			case 0:
				if (!walls.get(rotation)) {
				if (currentLocation.getX()<=9 && currentLocation.getX()<9) {
				for (Location visited:player.visitedLocations) {
					if (visited.getX()==currentLocation.getX() && visited.getY()==currentLocation.getY()+1) {
						return visited;
					}
				}
				
				return new Location(currentLocation.getX(),currentLocation.getY()+1);
				}
				}
				return currentLocation;
			
			case 90:
				if (!walls.get(rotation)) {
				if (currentLocation.getX()<9 && currentLocation.getX()<=9) {
				for (Location visited:player.visitedLocations) {
					if (visited.getX()==currentLocation.getX()+1 && visited.getY()==currentLocation.getY()) {
						return visited;
					}
				}
				return new Location(currentLocation.getX()+1,currentLocation.getY());
				}
				}
				return currentLocation;
			
			case 180:
				if (!walls.get(rotation)) {
				if (currentLocation.getX()>=0 && currentLocation.getX()>0) {
				for (Location visited:player.visitedLocations) {
					if (visited.getX()==currentLocation.getX() && visited.getY()==currentLocation.getY()-1) {
						return visited;
					}
				}
				return new Location(currentLocation.getX(),currentLocation.getY()-1);
				}
				}
				return currentLocation;
			
			case 270:
				if (!walls.get(rotation)) {
				if (currentLocation.getX()>0 && currentLocation.getX()>=0) {
				for (Location visited:player.visitedLocations) {
					if (visited.getX()==currentLocation.getX()-1 && visited.getY()==currentLocation.getY()) {
						return visited;
					}
				}
				
				return new Location(currentLocation.getX()-1,currentLocation.getY());
				}
				}
				return currentLocation;
			
			default:
				break;
			}
			return null;
		}
		*/
		public Map<Pair<Integer,Integer>,Integer> getTimesVisited() {
			return timesVisited;
		}
		public void setTimesVisited(Map<Pair<Integer,Integer>,Integer> timesVisited) {
			this.timesVisited = timesVisited;
		}
		public void addVisited(Pair<Integer,Integer> coord) {
			boolean value=false;
			for (Entry<Pair<Integer,Integer>,Integer> entry:timesVisited.entrySet()) {
				if (entry.getKey().getKey()==coord.getKey() && entry.getKey().getValue()==coord.getValue()) {
					timesVisited.put(coord, entry.getValue()+1);
					value=true;
				}
			}
			if (!value) {
				timesVisited.put(coord, 1);
			}
		}
}
public class GameState {
	private Player player;
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
	
	public Player getPlayer() {
		return player;
	}
	public void setPlayer(Player player) {
		this.player = player;
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
