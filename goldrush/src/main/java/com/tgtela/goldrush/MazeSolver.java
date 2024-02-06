package com.tgtela.goldrush;

import javax.websocket.ClientEndpoint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import java.io.IOException;
import java.net.URI;
import java.util.Random;
import java.util.Set;
import java.util.Stack;
import java.util.function.Predicate;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.ajax.JSON;
import org.eclipse.jetty.websocket.api.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javafx.util.Pair;

import org.eclipse.jetty.websocket.*;
import javax.websocket.server.ServerContainer;


public class MazeSolver 

{
	
	

	public static Action getTo(Location start, Location dest, int currentRotation) {
		Action action = null;
	    if (start.getX() == dest.getX() && start.getY() == dest.getY()) {
	        return action;
	    }

	    int rotation;

	    if (start.getX() == dest.getX() && start.getY() == dest.getY() + 1) {
	        rotation = 0;
	        if (currentRotation==rotation) {
	        	return null;
	        }
	        action=new RotateAction(0);
	        return action;
	    } else if (start.getX() == dest.getX() + 1 && start.getY() == dest.getY()) {
	        rotation = 90;
	        if (currentRotation==rotation) {
	        	return null;
	        }
	        action=new RotateAction(90);
	        return action;
	    } else if (start.getX() == dest.getX() && start.getY() == dest.getY() - 1) {
	        rotation = 180;
	        if (currentRotation==rotation) {
	        	return null;
	        }
	        action=new RotateAction(180);
	        return action;
	    } else if (start.getX() == dest.getX() - 1 && start.getY() == dest.getY()) {
	        rotation = 270;
	        if (currentRotation==rotation) {
	        	return null;
	        }
	        action=new RotateAction(270);
	        return action;
	    } else {
	        return null;
	    }

	    
	}

	    public static ActionGenerationResult generateAction(Player player,Action prevAction,GameState gameState) {
    		player.addLocation(gameState.getCurrent().getX(), gameState.getCurrent().getY());
    		player.addVisited(new Pair<Integer,Integer>(gameState.getCurrent().getX(),gameState.getCurrent().getY()));
    		
	    	if (prevAction==null) {
		    	if (gameState.getCurrent().whichNotWalls(gameState.getSquare()).contains(gameState.getCurrentRotation())) {
		    		ActionGenerationResult result=new ActionGenerationResult();
		        	Action newAction=new MoveAction(gameState.getCurrentRotation());
		        	result.setAction(newAction);
		        	result.setUpdatedPlayer(player);
		        	return result;
		    	}
		    	else if (!gameState.getCurrent().whichNotWalls(gameState.getSquare()).contains(gameState.getCurrentRotation())) {
		    		Integer rotation=exploreNeighbours(player,gameState,gameState.getCurrentRotation());
		    		Action nextAction=new RotateAction(rotation);
		    		ActionGenerationResult result=new ActionGenerationResult();
		    		result.setAction(nextAction);
		    		result.setUpdatedPlayer(player);
		    		return result;
		    	}
	    	}
	    	else if (prevAction instanceof RotateAction) {
	    		ActionGenerationResult result=new ActionGenerationResult();
	    		Action nextAction=new MoveAction(gameState.getCurrentRotation());
	    		result.setAction(nextAction);
	    		result.setUpdatedPlayer(player);
	    		return result;
	    	}
	    	
	    	ActionGenerationResult result1=dfs(player,gameState,gameState.getCurrentRotation());
	    	return result1;
	    }

	    private static ActionGenerationResult dfs(Player player,GameState gameState, int currentRotation) {
	    	if (player.getTimesVisited().get(new Pair<Integer,Integer>(gameState.getCurrent().getX(),gameState.getCurrent().getY()))>1 
	    			&& !gameState.getCurrent().getNeighbours().isEmpty()) {
	    		ActionGenerationResult result=new ActionGenerationResult();
	    		Action newAction=new RotateAction(player.getPosition().getNeighbours().get(0));
	    		result.setAction(newAction);
	        	result.setUpdatedPlayer(player);
	        	return result;
	    	}
	    	
	    	addNeighbours(gameState.getCurrent(),player,gameState);
	    	
	    	if (exploreNeighbours(player,gameState,gameState.getCurrentRotation())!=null) {
	    		Integer next=exploreNeighbours(player,gameState,gameState.getCurrentRotation());
	    		gameState.getCurrent().removeNeighbour(next);
	        	ActionGenerationResult result=new ActionGenerationResult();
	        	Action newAction=new RotateAction(next);
	        	result.setAction(newAction);
	        	result.setUpdatedPlayer(player);
	        	return result;
	        }
	    	else if(!gameState.getCurrent().getNeighbours().isEmpty()) {
	    		int rotation=gameState.getCurrent().getNeighbours().get(0);
	    		gameState.getCurrent().removeNeighbour(Integer.valueOf(rotation));
	    		ActionGenerationResult result=new ActionGenerationResult();
	        	Action newAction=new RotateAction(rotation);
	        	result.setAction(newAction);
	        	result.setUpdatedPlayer(player);
	        	return result;
	    		
	    		
	    	}
	    	else if (gameState.getCurrent().whichNotWalls(gameState.getSquare()).contains(gameState.getCurrentRotation())) {
	    		ActionGenerationResult result=new ActionGenerationResult();
	        	Action newAction=new MoveAction(gameState.getCurrentRotation());
	        	result.setAction(newAction);
	        	result.setUpdatedPlayer(player);
	        	return result;
	    	}
	    	else {
	    		System.out.println("moi");
	    		List<Integer> priority=prioritizeRotations(gameState.getCurrentRotation(),player,gameState);
	    		
	    		if (gameState.getCurrent().whichNotWalls(gameState.getSquare()).contains(priority.get(0))) {
	    			int rotation=priority.get(0);
	    	        Action newAction=new RotateAction(rotation);
	    	        ActionGenerationResult result=new ActionGenerationResult();
	    	        result.setAction(newAction);
	            	result.setUpdatedPlayer(player);
	            	return result;
	    	        
	    			
	    		}
	    		else if (gameState.getCurrent().whichNotWalls(gameState.getSquare()).contains(priority.get(1))) {
	    			int rotation=priority.get(1);
	    	        Action newAction=new RotateAction(rotation);
	    	        ActionGenerationResult result=new ActionGenerationResult();
	    	        result.setAction(newAction);
	            	result.setUpdatedPlayer(player);
	            	return result;
	    	        
	    			
	    		}
	    		else if (gameState.getCurrent().whichNotWalls(gameState.getSquare()).contains(priority.get(2))) {
	    			int rotation=priority.get(2);
	    	        Action newAction=new RotateAction(rotation);
	    	        ActionGenerationResult result=new ActionGenerationResult();
	    	        result.setAction(newAction);
	            	result.setUpdatedPlayer(player);
	            	return result;
	    	        
	    			
	    		}
	    		else if (gameState.getCurrent().whichNotWalls(gameState.getSquare()).contains(priority.get(3))) {
	    			int rotation=priority.get(3);
	    	        Action newAction=new RotateAction(rotation);
	    	        ActionGenerationResult result=new ActionGenerationResult();
	    	        result.setAction(newAction);
	            	result.setUpdatedPlayer(player);
	            	return result;
	    	        
	    			
	    		}
	    		ActionGenerationResult result=new ActionGenerationResult();
	    		return result;
	    		
	    	}
	    	
	    	
	  
	        
	        
	        
	    }
	    private static void addNeighbours(Coord currentLocation,Player player, GameState gameState) {
	    	List<Integer> neighbours=new ArrayList<Integer>();
	    	
	    	
	    	if (!player.isVisited(gameState.getCurrent().getX(),gameState.getCurrent().getY()+1) && 
	    			gameState.getCurrent().whichNotWalls(gameState.getSquare()).contains(180)){
	    		neighbours.add(180);


	    	}
	    	if (!player.isVisited(gameState.getCurrent().getX()+1,gameState.getCurrent().getY())&& 
	    			gameState.getCurrent().whichNotWalls(gameState.getSquare()).contains(90)){
	    		 neighbours.add(90);
	    
	    	}
	    	if (!player.isVisited(gameState.getCurrent().getX()-1,gameState.getCurrent().getY()) &&
	    			gameState.getCurrent().whichNotWalls(gameState.getSquare()).contains(270)){
	    		Coord coord=new Coord(gameState.getCurrent().getX()-1,gameState.getCurrent().getY());
	    		neighbours.add(270);
	    		
	    
	    	}
	    	if (!player.isVisited(gameState.getCurrent().getX(),gameState.getCurrent().getY()-1) 
	    			&& gameState.getCurrent().whichNotWalls(gameState.getSquare()).contains(0)){
	    		Coord coord=new Coord(gameState.getCurrent().getX(),gameState.getCurrent().getY()-1);
	    		neighbours.add(0);
	    		
	    
	    	}
	    	gameState.getCurrent().setNeighbours(neighbours);
	    	
	    }
	    private static Coord goStraight(GameState gameState, Location currentLocation,int currentRotation) {
	    	switch (currentRotation){
	    	case 0:
	    		return new Coord (gameState.getCurrent().getX(),gameState.getCurrent().getY()-1);
	    	
	    	case 90:
	    		return new Coord (gameState.getCurrent().getX()+1,gameState.getCurrent().getY());
	    		
	    	case 180:
	    		return new Coord (gameState.getCurrent().getX(),gameState.getCurrent().getY()+1);
	    	
	    	case 270:
	    		return new Coord (gameState.getCurrent().getX()-1,gameState.getCurrent().getY());
	    		
	    	}
	    	return null;
	    }
	    private static int chooseRotationNoWalls(List<Integer> notWalls,int currentRotation) {
	    	switch(currentRotation) {
	    	
	    	case 0:
	    		if (notWalls.contains(90)) {
	    		return 90;
	    		}
	    		else if (notWalls.contains(180)) {
	    			return 180;
	    		}
	    		else {
	    			return 270;
	    		}
	    	
	    	case 90:
	    		if (notWalls.contains(180)) {
		    		return 180;
		    		}
		    		else if (notWalls.contains(270)) {
		    			return 270;
		    		}
		    		else {
		    			return 0;
		    		}
		    	
	    		
	    	case 180:
	    		if (notWalls.contains(270)) {
		    		return 270;
		    		}
		    		else if (notWalls.contains(0)) {
		    			return 0;
		    		}
		    		else {
		    			return 90;
		    		}
		    	
	    	
	    	case 270:
	    		if (notWalls.contains(0)) {
		    		return 0;
		    		}
		    		else if (notWalls.contains(90)) {
		    			return 90;
		    		}
		    		else {
		    			return 180;
		    		}
		    	
	    		
	    	default: 
	    		return -1;
	    	}
	    	
	    }
	    private static Integer exploreNeighbours(Player player,GameState gameState, int currentRotation) {
	    	
	    	ArrayList<Integer> priority=prioritizeRotations(currentRotation,player,gameState);
	    	if (priority.isEmpty()) {
	    		return null;
	    	}
	    	
	    	if (gameState.getCurrent().whichNotWalls(gameState.getSquare()).contains(priority.get(0))){
	    		
	    		
		    	return priority.get(0);


	    	}
	    	if (gameState.getCurrent().whichNotWalls(gameState.getSquare()).contains(priority.get(1))){
	    		
		    		return priority.get(1);
	    		
	    
	    	}
	    	if (gameState.getCurrent().whichNotWalls(gameState.getSquare()).contains(priority.get(2))){
	    		return priority.get(2);
	    		
	    
	    	}
	    	if (gameState.getCurrent().whichNotWalls(gameState.getSquare()).contains(priority.get(3))){
	    		
	    		return priority.get(3);
	    		
	    		
	    
	    	}
	    	if (gameState.getCurrent().whichNotWalls(gameState.getSquare()).contains(currentRotation)) {
	    		return currentRotation;
	    	}
	    	System.out.println("moi");
	    	return null;
	    			
	    	
	    }
	    private static Coord coordMatchingRotation(int rotation,Coord current,Player player){
	    	switch (rotation) {
	    	case 0:
	    		if (current.isVisited()) {
	    			return player.getLocation(current.getX(), current.getY()-1);
	    		}
	    		return new Coord(current.getX(),current.getY()-1);
	    	
	    	case 90:
	    		if (player.isVisited(current.getX()+1,current.getY())) {
	    			return player.getLocation(current.getX()+1, current.getY());
	    		}
	    		return new Coord(current.getX()+1,current.getY());
	    	
	    	case 180:
	    		if (player.isVisited(current.getX(),current.getY()+1 )) {
	    			return player.getLocation(current.getX(), current.getY()+1);
	    		}
	    		return new Coord(current.getX(),current.getY()+1);
	    	
	    	case 270:
	    		if (player.isVisited(current.getX()-1,current.getY())) {
	    			return player.getLocation(current.getX()-1, current.getY());
	    		}
	    		return new Coord(current.getX()-1,current.getY());
	    	default: 
	    		break;
	    	
	    	}
	    	return null;
	    	
	    }

	    private static int chooseNewRotation(int currentRotation) {
	        // Choose a new rotation based on the current rotation
	        switch (currentRotation) {
	            case 0:
	                return 180;
	            case 90:
	                return 270;
	            case 180:
	                return 0;
	            case 270:
	                return 90;
	            default:
	                return 0;
	        }
	    }
	    private static ActionGenerationResult directionAction(GameState gameState,Player player) {
	    	Coord loc=player.getPosition();
    		ArrayList<Integer> walls=loc.whichNotWalls(gameState.getSquare());
    		int rotation=player.getRotation();
    		if (!walls.contains("North")) {
    			if (rotation==0) {
    				ActionGenerationResult result = new ActionGenerationResult();
    	            result.setAction(new MoveAction(player.getRotation()));
    	            result.setUpdatedPlayer(player);
    	            return result;
    			}
    			else {
    				ActionGenerationResult result = new ActionGenerationResult();
    	            result.setAction(new RotateAction(0));
    	            result.setUpdatedPlayer(player);
    	            return result;
    			}
    		}
    		if (!walls.contains("East")) {
    			if (rotation==90) {
    				ActionGenerationResult result = new ActionGenerationResult();
    	            result.setAction(new MoveAction(player.getRotation()));
    	            result.setUpdatedPlayer(player);
    	            return result;
    			}
    			else {
    				ActionGenerationResult result = new ActionGenerationResult();
    	            result.setAction(new RotateAction(90));
    	            result.setUpdatedPlayer(player);
    	            return result;
    			}
    		}
    		if (!walls.contains("West")) {
    			if (rotation==270) {
    				ActionGenerationResult result = new ActionGenerationResult();
    	            result.setAction(new MoveAction(player.getRotation()));
    	            result.setUpdatedPlayer(player);
    	            return result;
    			}
    			else {
    				ActionGenerationResult result = new ActionGenerationResult();
    	            result.setAction(new RotateAction(0));
    	            result.setUpdatedPlayer(player);
    	            return result;
    			}
    		}
    		if (!walls.contains("South")) {
    			if (rotation==180) {
    				ActionGenerationResult result = new ActionGenerationResult();
    	            result.setAction(new MoveAction(player.getRotation()));
    	            result.setUpdatedPlayer(player);
    	            return result;
    			}
    			else {
    				ActionGenerationResult result = new ActionGenerationResult();
    	            result.setAction(new RotateAction(180));
    	            result.setUpdatedPlayer(player);
    	            return result;
    			}
    		}
    		ActionGenerationResult result = new ActionGenerationResult();
    		return result;
	    }
	    
	    private static String getCurrentDirection(int currentRotation) {
	    	 switch (currentRotation) {
		        case 0:
		        	return "North";
		        case 90:
		        	return "East";
		        case 180:
		        	
		        	return "South";
		        case 270:
		        	
		        	return "West";
		        default:
		        	break;
		        }
		        return "Not a valid Direction";
	    }
	    private static List<Integer> prioritizeRotationsBegin(int currentRotation,Player player,GameState gameState){
	    	List<Integer> rotations;
	    	switch(currentRotation) {
	    	case 0:
	    		rotations=List.of(90,270,180,0);
	    		return rotations;
	    	case 90:
	    		rotations=Arrays.asList(180,0,270,90);
	    		return rotations;
	    	
	    	case 180:
	    		rotations=Arrays.asList(270,90,0,180);
	    		return rotations;
	    	case 270:
	    		rotations=Arrays.asList(0,180,90,270);
	    		return rotations;
	    	
	    	default:
	    		break;
	    	}
	    	return Collections.emptyList();
	    }

	    private static ArrayList<Integer> prioritizeRotations(int currentRotation,Player player,GameState gameState) {
	    	int visitedZero=0;
	    	int visitedNinety=0;
	    	int visitedOneEighty=0;
	    	int visitedTwoSeventy=0;
	    	ArrayList<Integer> rotations=new ArrayList<Integer>();
	    	ArrayList<Integer> visitedTimes=new ArrayList<Integer>();
	    	if (player.getLocation(gameState.getCurrent().getX(), gameState.getCurrent().getY()-1)!=null) {
        		visitedZero=player.getTimesVisited().get(new Pair<Integer,Integer>(gameState.getCurrent().getX(),gameState.getCurrent().getY()-1));
        	}
	    	else {
	    		Coord coord=new Coord(gameState.getCurrent().getX(),gameState.getCurrent().getY()-1);
	    		if (coord.whichNotWalls(gameState.getSquare()).contains(0)) {
	    			rotations.add(0);
	    			return rotations;
	    			
	    		}
	    	}
	    	if (player.getLocation(gameState.getCurrent().getX()+1, gameState.getCurrent().getY())!=null) {
	    		visitedNinety=player.getTimesVisited().get(new Pair<Integer,Integer>(gameState.getCurrent().getX()+1,gameState.getCurrent().getY()));
	    		
	    	}
	    	else {
	    		Coord coord=new Coord(gameState.getCurrent().getX()+1,gameState.getCurrent().getY());
	    		if (coord.whichNotWalls(gameState.getSquare()).contains(90)) {
	    			rotations.add(90);
	    			return rotations;
	    			
	    		}
	    	}
	    	if (player.getLocation(gameState.getCurrent().getX(), gameState.getCurrent().getY()+1)!=null) {
	    		visitedOneEighty=player.getTimesVisited().get(new Pair<Integer,Integer>(gameState.getCurrent().getX(),gameState.getCurrent().getY()+1));
	    		
	    	}
	    	else {
	    		Coord coord=new Coord(gameState.getCurrent().getX(),gameState.getCurrent().getY()+1);
	    		if (coord.whichNotWalls(gameState.getSquare()).contains(180)) {
	    			rotations.add(180);
	    			return rotations;
	    			
	    		}
	    	}
	    	if (player.getLocation(gameState.getCurrent().getX()-1, gameState.getCurrent().getY())!=null) {
	    		visitedTwoSeventy=player.getTimesVisited().get(new Pair<Integer,Integer>(gameState.getCurrent().getX()-1,gameState.getCurrent().getY()));
	    		
	    	}
	    	else {
	    		Coord coord=new Coord(gameState.getCurrent().getX()-1,gameState.getCurrent().getY());
	    		if (coord.whichNotWalls(gameState.getSquare()).contains(270)) {
	    			rotations.add(270);
	    			return rotations;
	    			
	    		}
	    	}
	    	visitedTimes.add(visitedZero);
	    	visitedTimes.add(visitedNinety);
	    	visitedTimes.add(visitedOneEighty);
	    	visitedTimes.add(visitedTwoSeventy);
	    	Collections.sort(visitedTimes);
	    	
	    	
	    	for (int visited:visitedTimes) {
	    		if (visited==visitedZero && !rotations.contains(0)) {
	    			rotations.add(0);
	    		}
	    		else if (visited==visitedNinety && !rotations.contains(90)) {
	    			rotations.add(90);
	    		}
	    		else if (visited==visitedOneEighty && !rotations.contains(180)) {
	    			rotations.add(180);
	    		}
	    		else if (visited==visitedTwoSeventy && !rotations.contains(270)) {
	    			rotations.add(270);
	    		}
	    	}
	    	return rotations;
	    }

    

	 public static class ActionGenerationResult {
		    private Action action;
		    private Player updatedPlayer;
		    public ActionGenerationResult() {
		    	
		    }

		    public Action getAction() {
		        return action;
		    }

		    public void setAction(Action action) {
		        this.action = action;
		    }

		    public Player getUpdatedPlayer() {
		        return updatedPlayer;
		    }

		    public void setUpdatedPlayer(Player updatedPlayer) {
		        this.updatedPlayer = updatedPlayer;
		    }
		}

	    public interface Action {
	        ActionType getType();
	    }

	    public enum ActionType {
	        MOVE, ROTATE
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
