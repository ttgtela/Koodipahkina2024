package com.tgtela.goldrush;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
public class WebSocketMessageParser {
	
    public static GameState parseGameState(String gameStateJson) {
    	try {
    		JsonFactory jsonFactory = new JsonFactory();
            JsonParser jsonParser = jsonFactory.createParser(gameStateJson);
            GameState gameState = new GameState();
            while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
                String field = jsonParser.getCurrentName();
                if ("player".equals(field)) {
                    jsonParser.nextToken();
                    gameState=processPlayer(jsonParser,gameState);
                    
                }
                else if ("moves".equals(field)) {
                    jsonParser.nextToken();
                    int moves = jsonParser.getIntValue();
                    gameState.setMoves(moves);
                } else if ("timer".equals(field)) {
                    jsonParser.nextToken();
                    int timer = jsonParser.getIntValue();
                    gameState.setTimer(timer);
                } else if ("start".equals(field)) {
                    jsonParser.nextToken();
                    Coord start=processLocation(jsonParser);
                    gameState.setStart(start);
                } else if ("startRotation".equals(field)) {
                    jsonParser.nextToken();
                    int startRotation = jsonParser.getIntValue();
                    gameState.setStartRotation(startRotation);
                } else if ("target".equals(field)) {
                    jsonParser.nextToken();
                    Coord target=processLocation(jsonParser);
                    gameState.setTarget(target);
                }
                else if ("square".equals(field)) {
                    jsonParser.nextToken();
                    int square = jsonParser.getIntValue();
                    gameState.setSquare(square);
                } else if ("rows".equals(field)) {
                    jsonParser.nextToken();
                    int rows = jsonParser.getIntValue();
                    gameState.setRows(rows);
                } else if ("columns".equals(field)) {
                    jsonParser.nextToken();
                    int columns = jsonParser.getIntValue();
                    gameState.setColumns(columns);
                }
            }
            return gameState;
                	
              
    	}
    	
    	catch (Exception e) {
            e.printStackTrace();
        }
    	return null;
		
    	
    }
    private static GameState processPlayer(JsonParser jsonParser,GameState gameState) throws IOException {
    	Integer rotation=null;
    	Coord location = null;
        while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
            String field = jsonParser.getCurrentName();

            if ("position".equals(field)) {
                jsonParser.nextToken();
                location=processLocation(jsonParser);
                gameState.setCurrent(location);
            } else if ("rotation".equals(field)) {
                jsonParser.nextToken();
                rotation = jsonParser.getIntValue();
                gameState.setCurrentRotation(rotation);
            }
        }
        return gameState;
    }
    private static Coord processLocation(JsonParser jsonParser) throws IOException {
    	Integer x = null;
    	Integer y=null;
        while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
            String field = jsonParser.getCurrentName();

            if ("x".equals(field)) {
                jsonParser.nextToken();
                x = jsonParser.getIntValue();
                
            } else if ("y".equals(field)) {
                jsonParser.nextToken();
                y = jsonParser.getIntValue();
            }
            
        }
        try {
        	return new Coord(x,y);
        }
        catch(Exception e) {
        	return null;
        }
    }
    public static GameInstance parseGameInstance(String gameInstanceJson) {
    	try {
    		JsonFactory jsonFactory = new JsonFactory();
            JsonParser jsonParser = jsonFactory.createParser(gameInstanceJson);
            GameInstance gameInstance = new GameInstance();
            while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
                String field = jsonParser.getCurrentName();
                if ("entityId".equals(field)) {
                    jsonParser.nextToken();
                    String entityId=jsonParser.getText();
                    gameInstance.setEntityId(entityId);
                    
                }
                else if ("gameState".equals(field)) {
                    jsonParser.nextToken();
                    String gameState = jsonParser.getText();
                    gameInstance.setGameState(parseGameState(gameState));
                } else if ("ownerId".equals(field)) {
                    jsonParser.nextToken();
                    String ownerId = jsonParser.getText();
                    gameInstance.setOwnerId(ownerId);
                } else if ("status".equals(field)) {
                    jsonParser.nextToken();
                    String status=jsonParser.getText();
                    gameInstance.setStatus(status);
                }
                else if ("reason".equals(field)) {
                    jsonParser.nextToken();
                    String reason=jsonParser.getText();
                    gameInstance.setReason(reason);
                }
                else if ("createdAt".equals(field)) {
                    jsonParser.nextToken();
                    String createdAt=jsonParser.getText();
                    gameInstance.setCreatedAt(createdAt);
                } 
                else if ("gameType".equals(field)) {
                    jsonParser.nextToken();
                    String gameType=jsonParser.getText();
                    gameInstance.setGameType(gameType);
                } 
                else if ("score".equals(field)) {
                    jsonParser.nextToken();
                    int score=jsonParser.getIntValue();
                    gameInstance.setScore(score);
                }
                else if ("levelId".equals(field)) {
                    jsonParser.nextToken();
                    String levelId=jsonParser.getText();
                    gameInstance.setLevelId(levelId);
                } 
            }
            return gameInstance;
                	
              
    	}
    	
    	catch (Exception e) {
            e.printStackTrace();
        }
    	return null;
    	
    }
}
