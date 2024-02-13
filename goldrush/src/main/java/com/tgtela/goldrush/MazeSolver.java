package com.tgtela.goldrush;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import javafx.util.Pair;
/**
 * @author Tenho Laakkio.
 * Code for the Monad 2024 Rekry challenge
 */

/**
 * a class for solving the games maze and generating actions to navigate through the maze.
 * It works by first mapping the entire maze until there is no more unvisited coords. It
 * does this by going to an unvisited neighbour or if there isn't one, it will find the
 * shortest path by actions to the first unvisited coords. If it accidentally stumbles
 * to the target coord before it has mapped the entire maze, it will perform the reset
 * actions so as to not end the game prematurely. After mapping the entire maze, it will
 * reset so that the move count goes to zero. Then it will find the shortest path to 
 * the target taking into account the cost of the actions between coords and 
 * also the manhattan distance.
 */
public class MazeSolver 

{
    static boolean targetFound=false;
    static boolean resetedBeforeFindingPath=false;
    static List<Action> shortestPath=new ArrayList<Action>();
    static List<Action> shortestPathToTarget=new ArrayList<Action>();
	

    /**
     * Determines the action and rotation needed to move from the start coordinate to the destination coordinate.
     *
     * @param start            The starting coordinate.
     * @param dest             The destination coordinate.
     * @param currentRotation  The current rotation.
     * @return A Pair containing the action and rotation to move from start to dest.
     */
	public static Pair<Action,Integer> getTo(Coord start, Coord dest, int currentRotation) {
		Action action = null;
	    int rotation = -1;

	    if (start.getX() == dest.getX() && start.getY() == dest.getY()) {
	        // No need to move if start and dest are the same
	        return null;
	    }

	    if (start.getX() == dest.getX() && start.getY()-1 == dest.getY()) {
	        rotation = 0;
	    } else if (start.getX() + 1 == dest.getX() && start.getY() == dest.getY()) {
	        rotation = 90;
	    } else if (start.getX() == dest.getX() && start.getY()+1 == dest.getY()) {
	        rotation = 180;
	    } else if (start.getX() - 1 == dest.getX() && start.getY() == dest.getY()) {
	        rotation = 270;
	    }

	    if (rotation != -1 && rotation != currentRotation) {
	        action = new RotateAction(rotation);
	    } else {
	        rotation = currentRotation;
	    }
	    return new Pair<>(action, rotation);

	    
	}
	 /**
     * Sets the discovered status of the neighboring cells of the current position in the maze.
     *
     * @param maze      The maze object.
     * @param coord     The current coordinate.
     * @param gameState The game state.
     * @return The updated maze object.
     */
	public static Maze setNeighbours(Maze maze,Coord coord,GameState gameState) {
		for (Coord neighbours:maze.canGetToCoord(maze.getCurrentPosition().getY(),maze.getCurrentPosition().getX(), maze.getCurrentPosition().getSquare())) {
			maze.getGrid()[neighbours.getY()][neighbours.getX()].setDiscovered(true);
			if (maze.getGrid()[neighbours.getY()][neighbours.getX()].getX()==maze.getTarget().getX()
					&& maze.getGrid()[neighbours.getY()][neighbours.getX()].getY()==maze.getTarget().getY()){
						targetFound=true;
						 
						
					}
		}
		return maze;
	}
	 /**
     * Checks if there are any unvisited neighboring cells in the maze.
     *
     * @param maze      The maze object.
     * @param gameState The game state.
     * @return True if there are unvisited neighbors, false otherwise.
     */
		public static boolean anyGoodNeighbours(Maze maze,GameState gameState) {
			for (Coord neighbour:maze.canGetToCoord(maze.getCurrentPosition().getY(), 
					maze.getCurrentPosition().getX(), maze.getCurrentPosition().getSquare())) {
				if (!maze.getGrid()[neighbour.getY()][neighbour.getX()].isVisited()) {
					return true;
				}
				
			}
			return false;
		}

	    /**
	     * Sets up the maze with the current game state.
	     *
	     * @param maze      The maze object.
	     * @param gameState The game state.
	     * @return The updated maze object.
	     */
		public static Maze setMaze(Maze maze,GameState gameState) {
			maze.setCurrentPosition(maze.getGrid()[gameState.getCurrent().getY()][gameState.getCurrent().getX()]);
	    	maze.setCurrentRotation(gameState.getCurrentRotation());
	    	maze.getCurrentPosition().setDiscovered(true);
	    	maze.getCurrentPosition().setVisited(true);
	    	maze.getCurrentPosition().setTimesVisited(maze.getCurrentPosition().getTimesVisited()+1);
	    	maze.getCurrentPosition().setSquare(gameState.getSquare());
	    	maze=setNeighbours(maze,maze.getGrid()[maze.getCurrentPosition().getY()][maze.getCurrentPosition().getX()],gameState);
	    	return maze;
			
		}
		
		 /**
	     * Generates the next action based on the current maze, previous action, and game state.
	     *
	     * @param maze      The maze object.
	     * @param prevAction The previous action.
	     * @param gameState The game state.
	     * @param resetMode True if in reset mode, false otherwise.
	     * @return An ActionGenerationResult containing the next action and the updated maze.
	     */
	    
	    public static ActionGenerationResult generateAction(Maze maze,Action prevAction,GameState gameState,boolean resetMode) {
	    	if (prevAction==null) {
	    		maze=new Maze(gameState.getRows(),gameState.getColumns());
	    		maze.setTarget(gameState.getTarget().getY(), gameState.getTarget().getX());
 	    	}
	    	

	    	maze=setMaze(maze,gameState);
	    	
	    	
	    	if (!anyGoodNeighbours(maze,gameState)) {
	    		maze.setBackTrack(true);
	    	}
	    	else {
	    		maze.setBackTrack(false);
	    		shortestPath.clear();
	    	}
	    	if (targetFound==true && maze.isDiscoveredUnvisited()) {
	    		ActionGenerationResult result=new ActionGenerationResult();
	    		result.setAction(new ResetAction());
	    		result.setUpdatedMaze(maze);
	    		targetFound=false;
	    		return result;
	    	}
	    	else if (!maze.isDiscoveredUnvisited()) {
	    		maze.setBackTrack(false);
	    		if (resetedBeforeFindingPath) {
	    			maze=setMaze(maze,gameState);
	    		ActionGenerationResult result=reset(maze,gameState,prevAction);
	    		return result;
	    		}
	    		else {
	    			resetedBeforeFindingPath=true;
	    			ActionGenerationResult result=new ActionGenerationResult();
	    			maze=setMaze(maze,gameState);
	    			result.setAction(new ResetAction());
	    			result.setUpdatedMaze(maze);
	    			return result;
	    		}
	    	}
	    	
	    	
	    	
	    	
	    	
	    	if (maze.isBackTrack()) {
	    		ActionGenerationResult result=backTrack(maze,gameState,prevAction);
	    		return result;
	    		
	    		}

    		ActionGenerationResult result=solve(gameState,maze);
	    	return result;
	    }
	    
	    /**
	     * Solves the maze by generating the next action based on the current game state and maze.
	     *
	     * @param gameState The game state.
	     * @param maze      The maze object.
	     * @return An ActionGenerationResult containing the next action and the updated maze.
	     */
	    public static ActionGenerationResult solve(GameState gameState,Maze maze) {
	    	ActionGenerationResult result=new ActionGenerationResult();
	    	for (Coord neighbour:maze.canGetToCoord(maze.getCurrentPosition().getY(),maze.getCurrentPosition().getX(), maze.getCurrentPosition().getSquare())) {
	    		if (maze.getGrid()[neighbour.getY()][neighbour.getX()].isDiscovered() && !maze.getGrid()[neighbour.getY()][neighbour.getX()].isVisited()) {
	    			Pair<Action,Integer> pair=getTo(maze.getGrid()[maze.getCurrentPosition().getY()][maze.getCurrentPosition().getX()],
    						maze.getGrid()[neighbour.getY()][neighbour.getX()],maze.getCurrentRotation());
	    			if (pair!=null) {
	    				
	    				if (pair.getValue()==maze.getCurrentRotation()) {
	    					Action nextAction=new MoveAction(maze.getCurrentRotation());
	    					result.setAction(nextAction);
	    					result.setUpdatedMaze(maze);
	    					return result;
	    				}
	    				result.setAction(pair.getKey());
	    				result.setUpdatedMaze(maze);

	    				return result;
	    				
	    				
	    			}
	    
	    		}
			}
	    	
	    	if (result.getAction()==null) {
	    		result.setAction(new MoveAction(maze.getCurrentRotation()));
				result.setUpdatedMaze(maze);
	    		
	    	}
	    	return result;
	    }

	    /**
	     * Performs backtracking in the maze.
	     *
	     * @param maze      The maze object.
	     * @param gameState The game state.
	     * @param prevAction The previous action.
	     * @return An ActionGenerationResult containing the next action and the updated maze.
	     */
	    private static ActionGenerationResult backTrack(Maze maze,GameState gameState,Action prevAction) {
	    	if (shortestPath.isEmpty() || shortestPath==null) {
    			shortestPath=shortestPathToUnvisited(maze,maze.getCurrentPosition());
    		}
	    	
	    	Action nextAction=shortestPath.get(0);
    		shortestPath.remove(0);
    		if (nextAction!=null) {
    		
    		ActionGenerationResult result=new ActionGenerationResult();
			result.setAction(nextAction);
			result.setUpdatedMaze(maze);
			return result;
    		}

	    
    		
    		return new ActionGenerationResult();
	    }
	    
	    /**
	     * Resets the maze to find the shortest path to the target.
	     *
	     * @param maze      The maze object.
	     * @param gameState The game state.
	     * @param prevAction The previous action.
	     * @return An ActionGenerationResult containing the next action and the updated maze.
	     */	
	    private static ActionGenerationResult reset(Maze maze,GameState gameState,Action prevAction) {
	    	if (shortestPathToTarget.isEmpty() || shortestPathToTarget==null) {
    			shortestPathToTarget=shortestPathToTarget(maze,maze.getCurrentPosition(),maze.getTarget());
    		}
	    	
	    	Action nextAction=shortestPathToTarget.get(0);
    		shortestPathToTarget.remove(0);
    		if (nextAction!=null) {
    		
    		ActionGenerationResult result=new ActionGenerationResult();
			result.setAction(nextAction);
			result.setUpdatedMaze(maze);
			return result;
    		}

	    
    		
    		return new ActionGenerationResult();
	    }
	    	

	    /**
	     * Finds the shortest path to the target in the maze.
	     *
	     * @param maze  The maze object.
	     * @param start The starting coordinate.
	     * @param target The target coordinate.
	     * @return The shortest path to the target.
	     */
	    public static List<Action> shortestPathToTarget(Maze maze, Coord start, Coord target) {
	        PriorityQueue<Node> open = new PriorityQueue<>(Comparator.comparingInt(Node::getTotalCost));
	        Map<Node, Node> visited = new HashMap<>();
	        Set<Coord> visitedCoords = new HashSet<>();
	        int currentRotation=maze.getCurrentRotation();
	        open.offer(new Node(start, null, 0, calculateDistance(start, target),currentRotation));
	        
	        while (!open.isEmpty()) {
	            Node current = open.poll();
	            currentRotation=current.getCurrentRotation();
	            if (current.getCoord().equals(target)) {
	                return reconstructPathToTarget(current, visited,maze);
	                
	            }

	            
	            visitedCoords.add(current.getCoord());
	            


	            for (Pair<Integer, Coord> pair : maze.canGetToCoordWithRotationTarget(current.getCoord().getY(), 
	            		current.getCoord().getX(), current.getCoord().getSquare())) {
	                Coord nextCoord = pair.getValue();
	                if (visitedCoords.contains(nextCoord)) {
	                	continue;
	                }
	                
	                int rotation=pair.getKey();
	                Action action;
	                if (rotation==currentRotation) {
	                	action=new MoveAction(currentRotation);
	                }
	                else {
	                	action=new RotateAction(rotation);
	                }
	                int newCost=0;
	                if (action instanceof RotateAction) {
	                newCost = current.getCost() + 2;
	                
	                }
	                else {
	                	newCost = current.getCost() + 1;
	                }
	                int heuristic = calculateDistance(nextCoord, target);
	                Node newNode=new Node(nextCoord, action, newCost, heuristic,rotation);
	                visited.put(newNode, current);
	                open.offer(newNode);
	            }
	        }

	        return Collections.emptyList(); // No path found
	    }
	    
	    /**
	     * Finds the shortest path to an unvisited cell in the maze.
	     *
	     * @param maze  The maze object.
	     * @param start The starting coordinate.
	     * @return The shortest path to an unvisited coordinate.
	     */
	    public static List<Action> shortestPathToUnvisited(Maze maze, Coord start) {
	    	if (maze.isDiscoveredUnvisited()) {
	        PriorityQueue<Node> open = new PriorityQueue<>(Comparator.comparingInt(Node::getTotalCost));
	        Map<Node, Node> visited = new HashMap<>();
	        Set<Coord> visitedCoords = new HashSet<>();
	        int currentRotation=maze.getCurrentRotation();
	        
	        open.offer(new Node(start, null, 0, 0, currentRotation));
	        
	        while (!open.isEmpty()) {
	            Node current = open.poll();
	            currentRotation=current.getCurrentRotation();
	            

	            if (!current.getCoord().isVisited()) {
	                return reconstructPathToTarget(current, visited,maze);
	            }

	            visitedCoords.add(current.getCoord());


	            for (Pair<Integer, Coord> pair : maze.canGetToCoordWithRotation(current.getCoord().getY(), 
	            		current.getCoord().getX(), current.getCoord().getSquare())) {
	                Coord nextCoord = pair.getValue();
	                if (visitedCoords.contains(nextCoord)) {
	                	continue;
	                }
	                if (nextCoord==maze.getTarget()) {
	                	continue;
	                }
	                
	                
	                int rotation=pair.getKey();
	                Action action;
	                if (rotation==currentRotation) {
	                	action=new MoveAction(currentRotation);
	                }
	                else {
	                	action=new RotateAction(rotation);
	                }
	                int newCost=0;
	                if (action instanceof RotateAction) {
	                newCost = current.getCost()+2;
	                
	                }
	                else {
	                	newCost = current.getCost()+1;
	                }
	                
	                int heuristic =0;
	                Node newNode=new Node(nextCoord, action, newCost, heuristic,rotation);
	                visited.put(newNode, current);
	                visited.put(newNode, current);
	                open.offer(newNode);
	            }
	        }
	    	}

	        return Collections.emptyList(); // No path found
	    }


	    /**
	     * Reconstructs the path to the target node.
	     *
	     * @param endNode The end node.
	     * @param visited The visited nodes.
	     * @param maze    The maze object.
	     * @return The reconstructed path.
	     */
	    private static List<Action> reconstructPathToTarget(Node endNode, Map<Node, Node> visited,Maze maze) {
	        List<Action> path = new ArrayList<>();
	        Node current = endNode;
	        while (current.getCoord()!=null) {
	        	if (current.getAction() instanceof RotateAction) {
	        		path.add(new MoveAction(current.getCurrentRotation()));
	        		path.add(current.getAction());

	        	}
	        	else {
	        		path.add(current.getAction());
	        	}
	            
	            current = visited.get(current);
	            if (current.getCoord()==maze.getCurrentPosition()) {
	            	break;
	            }
	        }

	        Collections.reverse(path);
	        return path;
	    }
	    /**
	     * Calculates the Manhattan distance between two coordinates.
	     *
	     * @param current The current coordinate.
	     * @param target  The target coordinate.
	     * @return The Manhattan distance between the two coordinates.
	     */

	    private static int calculateDistance(Coord current, Coord target) {
	        
	    	return Math.abs(target.getX() - current.getX()) + Math.abs(target.getY() - current.getY()); 
	    }
	    
	    
	    
	    public static class Node {
	        private final Coord coord;
	        private final Action action;
	        private final int currentRotation;
	        private final int cost;
	        private final int heuristic;
	        

	        public Node(Coord coord, Action action, int cost, int heuristic,int currentRotation) {
	            this.coord = coord;
	            this.action = action;
	            this.cost = cost;
	            this.heuristic = heuristic;
	            this.currentRotation=currentRotation;
	            
	        }

	        public Coord getCoord() {
	            return coord;
	        }

	        public Action getAction() {
	            return action;
	        }

	        public int getCost() {
	            return cost;
	        }

	        public int getHeuristic() {
	            return heuristic;
	        }

	        public int getTotalCost() {
	            return cost+heuristic;
	        }

			public int getCurrentRotation() {
				return currentRotation;
			}
			
			
	    }
	    
	    
	    
    

	 public static class ActionGenerationResult {
		    private Action action;
		    private Maze updatedMaze;
		    public ActionGenerationResult() {
		    	
		    }

		    public Action getAction() {
		        return action;
		    }
		    public void setAction(Action action) {
		        this.action = action;
		    }

	
		    public Maze getUpdatedMaze() {
		    	return updatedMaze;
		    }
		    public void setUpdatedMaze(Maze maze) {
		    	this.updatedMaze=maze;
		    }
		}

	    public interface Action {
	        ActionType getType();
	    }

	    public enum ActionType {
	        MOVE, ROTATE, RESET
	    }

	    public static class MoveAction implements Action {
	        private final ActionType type = ActionType.MOVE;
	        private final int rotation;

	        public MoveAction(int rotation) {
	            this.rotation = rotation;
	        }
	        public int getRotation() {
	            return rotation;
	        }
	        @Override
	        public ActionType getType() {
	            return type;
	        }

	        @Override
	        public String toString() {
	            return "MoveAction";
	        }
	    }
	    public static class ResetAction implements Action {
	        private final ActionType type = ActionType.RESET;

	      
	        @Override
	        public ActionType getType() {
	            return type;
	        }

	        @Override
	        public String toString() {
	            return "MoveAction";
	        }
	    }

	    public static class RotateAction implements Action {
	        private final ActionType type = ActionType.ROTATE;
	        private final int rotation;

	        public RotateAction(int rotation) {
	            this.rotation = rotation;
	        }

	        public int getRotation() {
	            return rotation;
	        }

	        @Override
	        public ActionType getType() {
	            return type;
	        }

	        @Override
	        public String toString() {
	            return "RotateAction{" +
	                    "rotation=" + rotation +
	                    '}';
	        }
	    }


}
