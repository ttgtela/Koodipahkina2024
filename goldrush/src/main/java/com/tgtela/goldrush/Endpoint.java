package com.tgtela.goldrush;
import javax.websocket.OnMessage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.servlet.http.HttpUtils;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import javax.websocket.ClientEndpoint;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import java.net.URI;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.*;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tgtela.goldrush.MazeSolver.Action;
import com.tgtela.goldrush.MazeSolver.ActionGenerationResult;
import com.tgtela.goldrush.MazeSolver.MoveAction;
import com.tgtela.goldrush.MazeSolver.RotateAction;
import com.google.gson.JsonArray;

@ServerEndpoint("/game-state")
@ClientEndpoint
public class Endpoint {
    private ObjectMapper objectMapper = new ObjectMapper();
    
    private static String gameInstanceString="";
    private static Action action=null;
    private static Player player;

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("Connected to WebSocket server");

        sendWebSocketMessage(session, "sub-game", String.format("{\"id\":\"%s\"}",gameInstanceString));
    }
    @OnMessage
    public void onMessage(String message, Session session) {
    	
    	System.out.println("Received message: " + message);
        messageParser(message, session);

        try {
            JsonArray jsonObject = JsonParser.parseString(message).getAsJsonArray();

            if (jsonObject.isJsonArray() && jsonObject.getAsJsonArray().size() == 2) {
                String actionName = jsonObject.getAsJsonArray().get(0).getAsString();
                JsonObject payloadObject = jsonObject.getAsJsonArray().get(1).getAsJsonObject();

            } else {
                System.out.println("Invalid JSON format for WebSocket message");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
    public static void sendWebSocketMessage(Session session, String actionName, String payload) {
        String message = "[" + "\"" + actionName + "\", " + payload + "]";
        System.out.println(message);
        session.getAsyncRemote().sendText(message);
    }
    public static void sendGeneratedMessage(Session session,String message) {
    	System.out.println(message);
    	session.getAsyncRemote().sendText(message);
    }
    public static void messageParser(String message,Session session) {
    	
    	 try {
             JsonArray jsonObject = JsonParser.parseString(message).getAsJsonArray();

             if (jsonObject.isJsonArray() && jsonObject.getAsJsonArray().size() == 2) {
                 String actionName = jsonObject.getAsJsonArray().get(0).getAsString();
                 JsonObject payloadObject = jsonObject.getAsJsonArray().get(1).getAsJsonObject();

                 switch (actionName) {
                     case "sub-game":
                         handleSubGameAction(payloadObject, session);
                         break;
                     case "game-instance":
                         handleGameInstanceAction(payloadObject, session);
                         break;
                     case "run-command":
                         handleRunCommandAction(payloadObject, session);
                         break;
                     case "success":
                         handleSuccessAction(payloadObject, session);
                         break;
                     case "failure":
                         handleFailureAction(payloadObject, session);
                         break;
                     default:
                         System.out.println("Unknown action: " + actionName);
                 }
             } else {
                 System.out.println("Invalid JSON format for WebSocket message");
             }
         } catch (Exception e) {
             e.printStackTrace();
         }
        
   }
   	private static void handleSubGameAction(JsonObject payloadNode,Session session) {
           String gameId = payloadNode.get("id").getAsString();
       }

       private static void handleGameInstanceAction(JsonObject payloadNode, Session session) throws InterruptedException  {
    	   String gameInstanceString = payloadNode.toString();
    	    GameInstance gameInstance = WebSocketMessageParser.parseGameInstance(gameInstanceString);
    	    GameState gameState = gameInstance.getGameState(); 
    	    

    	    if (gameState.getTarget().getX() == gameState.getCurrent().getX() &&
    	        gameState.getTarget().getY() == gameState.getCurrent().getY()) {
    	        end(session);
    	    }
    	    if (action == null) {
    	    	player=new Player(gameState.getStart(),gameState.getStartRotation());
    	    	
    	    	ActionGenerationResult result = MazeSolver.generateAction(player, null,gameState);
    	    	
    	        action = result.getAction();
    	        player=result.getUpdatedPlayer();

    	    	}
    	    	
    	    	
    	    else {
    	    	player.getPosition().setX(gameState.getCurrent().getX());
    	    	player.getPosition().setY(gameState.getCurrent().getX());
    	    	player.setRotation(gameState.getCurrentRotation());
    	        ActionGenerationResult result = MazeSolver.generateAction(player, action,gameState);
    	        player=result.getUpdatedPlayer();
    	        action = result.getAction();
    	    }

    	    if (action instanceof MoveAction) {
    	        String actionString2 = String.format("{\"action\": \"move\"}}");
    	        String actionString = String.format("{\"gameId\": \"%s\"" + ",\"payload\":%s", gameInstance.getEntityId(), actionString2);
    	        sendWebSocketMessage(session, "run-command", actionString);
    	    } else if (action instanceof RotateAction) {
    	        int rotation = ((RotateAction) action).getRotation();
    	        String actionString2 = String.format("{\"action\": \"rotate\",\"rotation\": %d}}", rotation);
    	        String actionString = String.format("{\"gameId\": \"%s\"" + ",\"payload\":%s", gameInstance.getEntityId(), actionString2);
    	        sendWebSocketMessage(session, "run-command", actionString);
    	        Thread.sleep(300);
    	    }
    	    else if (action==null) {
    	    	System.out.println("Fail");
    	    }
    	    else {
    	        throw new IllegalArgumentException("Unsupported action type");
    	    }
    	    
           
       }

       private static void handleRunCommandAction(JsonObject payloadNode,Session session) {
    	      String gameId = payloadNode.get("gameId").getAsString();
    	      JsonObject payload = payloadNode.getAsJsonObject("payload");
       }

       private static void handleSuccessAction(JsonObject payloadNode,Session session) {
           String message = payloadNode.get("message").getAsString();
           System.out.println("Success message: " + message);
       }

       private static void handleFailureAction(JsonObject payloadNode,Session session) {
           String reason = payloadNode.get("reason").getAsString();
           String desc = payloadNode.has("desc") ? payloadNode.get("desc").getAsString() : "";
           System.out.println("Failure reason: " + reason);
           System.out.println("Description: " + desc);
       }
  
    public static GameInstance createGame(String levelId, String token) throws IOException {
        // Specify the API endpoint
        String apiUrl = "https://goldrush.monad.fi/backend/api/levels/" + levelId;

        // Create a URL object
        URL url = new URL(apiUrl);

        // Open a connection to the URL
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      
        connection.setRequestMethod("POST");
        // Set the Authorization header
        connection.setRequestProperty("Authorization", token);

        // Get the response code
        int responseCode = connection.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_CREATED) {
        	ObjectMapper objectMapper = new ObjectMapper();
        	String jsonResponse = readResponse(connection);
        	String gameInstanceResponse = connection.getContent().toString();
            System.out.println("Fetched game instance: " + gameInstanceResponse);
            return  WebSocketMessageParser.parseGameInstance(jsonResponse);
            
        } else {
            // Handle the error case
            System.err.println("Couldn't create game: " + connection.getResponseMessage());
            return null;
        }
    }
    private static String readResponse(HttpURLConnection connection) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            return response.toString();
        }
    }
    public static void end(Session session) {
    	
    }
    public static void main( String[] args ) throws IOException
    {
    	String levelId="01HENHM60KEHSGN4QSFT9MJ4CX";
    	String token="c6c27d12-1c54-4205-b257-a3281c77d3cf";
    	GameInstance gameInstance=createGame(levelId,token);
    	if (gameInstance!=null) {
    		gameInstanceString=gameInstance.getEntityId();
    		
        try {
        	URI serverUrl = new URI("wss://goldrush.monad.fi/backend/c6c27d12-1c54-4205-b257-a3281c77d3cf");
        	javax.websocket.WebSocketContainer container = javax.websocket.ContainerProvider.getWebSocketContainer();
        	 Session session = container.connectToServer(Endpoint.class, serverUrl);
        	 Thread.sleep(200000000);
        	 
        	
        }
        catch(Exception e) {
        	e.printStackTrace();
        }
    }
    }

}
