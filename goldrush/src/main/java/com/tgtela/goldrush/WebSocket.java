package com.tgtela.goldrush;
import javax.websocket.OnMessage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CompletableFuture;

import javax.websocket.*;
import javax.websocket.OnOpen;
import java.net.URI;
import java.net.URISyntaxException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tgtela.goldrush.MazeSolver.Action;
import com.tgtela.goldrush.MazeSolver.ActionGenerationResult;
import com.tgtela.goldrush.MazeSolver.MoveAction;
import com.tgtela.goldrush.MazeSolver.ResetAction;
import com.tgtela.goldrush.MazeSolver.RotateAction;
import com.google.gson.JsonArray;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

/**
 * @author Tenho Laakkio.
 * Code for the Monad 2024 Rekry challenge
 */
public class WebSocket extends WebSocketClient {
    
    private static String gameInstanceString="";
    private static Action action=null;
    private static Maze maze;
    private static boolean resetMode=false;
    private CompletableFuture<Void> webSocketFuture;
    
    /**
     * Constructor to initialize the WebSocket client with the server URL.
     * 
     * @param serverUrl The URI of the WebSocket server.
     */
    public WebSocket(URI serverUrl) {
        super(serverUrl);
        this.webSocketFuture = new CompletableFuture<>();
    }
    public CompletableFuture<Void> getWebSocketFuture() {
        return webSocketFuture;
    }
    
    
    /**
     * Handles the WebSocket connection opening event.
     */
    @OnOpen
    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("Connected to WebSocket server");
        String sendText=String.format("{\"id\":\"%s\"}",gameInstanceString);
        String actionName="sub-game";
        String sendMessage= "[" + "\"" + actionName + "\", " + sendText + "]";
        System.out.println("Sent message: "+sendMessage);
        send(sendMessage);
    }
    
    /**
     * Handles the WebSocket message received event.
     */
    @OnMessage
    @Override
    public void onMessage(String message) {
    	
    	System.out.println("Received message: " + message);
    	if (message.contains("failure")) {
    		close();
    	}
    	else {
        String sendText=messageParser(message);
        if (sendText.contains("success")){
        	 String actionName="success";
        	 String sendMessage = "[" + "\"" + actionName + "\", " + sendText + "]";
        	 send(sendMessage);
        	 System.out.println("Sent message: "+sendMessage);
        	 close();
        }
        else {
        String actionName="run-command";
        String sendMessage = "[" + "\"" + actionName + "\", " + sendText + "]";
        System.out.println("Sent message: "+sendMessage);
        send(sendMessage);
        }
    	}

       
    }
    /**
     * Handles the WebSocket connection closing event.
     */
    @OnClose
    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("WebSocket connection closed");
        
    }
	
    
    /**
     * Handles WebSocket error events.
     */
    @Override
    public void onError(Exception ex) {
        System.err.println("WebSocket error occurred: " + ex.getMessage());
        webSocketFuture.completeExceptionally(ex);
    }
 

    
    /**
     * Parses the incoming message received through the WebSocket connection.
     * 
     * @param message The JSON-formatted message received from the WebSocket server.
     * @return The parsed message as a String, which will be sent
     * to the websocket, formatted according to the game's requirements.
     */
    public static String messageParser(String message) {
    	String sendText="";
    	
    	 try {
             JsonArray jsonObject = JsonParser.parseString(message).getAsJsonArray();

             if (jsonObject.isJsonArray() && jsonObject.getAsJsonArray().size() == 2) {
                 String actionName = jsonObject.getAsJsonArray().get(0).getAsString();
                 JsonObject payloadObject = jsonObject.getAsJsonArray().get(1).getAsJsonObject();

                 switch (actionName) {

                     case "game-instance":
                         sendText=handleGameInstanceAction(payloadObject);
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
    	 return sendText;
        
   }
   	
    
    /**
     * Handles the action received in the WebSocket message payload.
     * Parses the game instance information, generates the appropriate action based on the game state,
     * and formats the action payload accordingly to send back to the WebSocket server.
     * 
     * @param payloadNode The JSON object containing the game instance information received in the WebSocket message.
     * @return The formatted action payload as a String to be sent back to the WebSocket server.
     * @throws InterruptedException If the thread sleep is interrupted while waiting.
     */
       private static String handleGameInstanceAction(JsonObject payloadNode) throws InterruptedException  {
    	   String sendText="";
    	   String gameInstanceString = payloadNode.toString();
    	    GameInstance gameInstance = WebSocketMessageParser.parseGameInstance(gameInstanceString);
    	    GameState gameState = gameInstance.getGameState(); 
    	    if (gameState.getCurrent().getX()==gameState.getTarget().getX() && gameState.getCurrent().getY()==gameState.getTarget().getY() && resetMode) {
    	    	String success=String.format("success! You won the game!",gameInstanceString);
    	    	return success;
    	       
    	    }
    	    else {
    	    ActionGenerationResult result=new ActionGenerationResult();
    	    if (action==null) {
    	    	result=MazeSolver.generateAction(null, null, gameState,resetMode);
    	    }
    	    else {
    	    	result=MazeSolver.generateAction(maze, action, gameState,resetMode);
    	    }
    	    action=result.getAction();
    	    maze=result.getUpdatedMaze();
    	    if (action instanceof MoveAction) {
    	        String actionString2 = String.format("{\"action\": \"move\"}}");
    	        String actionString = String.format("{\"gameId\": \"%s\"" + ",\"payload\":%s", gameInstance.getEntityId(), actionString2);
    	        sendText=actionString;
    	        Thread.sleep(20);
    	    } else if (action instanceof RotateAction) {
    	        int rotation = ((RotateAction) action).getRotation();
    	        String actionString2 = String.format("{\"action\": \"rotate\",\"rotation\": %d}}", rotation);
    	        String actionString = String.format("{\"gameId\": \"%s\"" + ",\"payload\":%s", gameInstance.getEntityId(), actionString2);
    	        sendText=actionString;
    	        Thread.sleep(20);
    	    }
    	    else if (action instanceof ResetAction) {
    	    	 String actionString2 = String.format("{\"action\": \"reset\"}}");
    	    	
    	    	 String actionString = String.format("{\"gameId\": \"%s\"" + ",\"payload\":%s", gameInstance.getEntityId(), actionString2);
    	    	sendText=actionString;
    	    	Thread.sleep(20);
    	    	resetMode=true;
    	    }
    	    }
    	return sendText;
    
           
       }

       /**
        * Creates a new game instance by sending a POST request to the specified API endpoint.
        * 
        * @param levelId The ID of the level for which a game instance is to be created.
        * @param token   The authorization token required for accessing the API.
        * @return The created GameInstance object containing the game instance information.
        * @throws IOException If an I/O error occurs while sending the HTTP request.
        */
    public static GameInstance createGame(String levelId, String token) throws IOException {
        String apiUrl = "https://goldrush.monad.fi/backend/api/levels/" + levelId;
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Authorization", token);
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_CREATED) {
        	String jsonResponse = readResponse(connection);
        	String gameInstanceResponse = connection.getContent().toString();
            System.out.println("Fetched game instance: " + gameInstanceResponse);
            return  WebSocketMessageParser.parseGameInstance(jsonResponse);
            
        } else {
            System.err.println("Couldn't create game: " + connection.getResponseMessage());
            return null;
        }
    }
    /**
     * Reads the response from an HttpURLConnection and constructs a String representation of the response body.
     * 
     * @param connection The HttpURLConnection object from which to read the response.
     * @return A String representation of the response body.
     * @throws IOException If an I/O error occurs while reading the response.
     */
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
    
    public static void main( String[] args ) throws IOException
    {
    	String levelId="01HENHM60KSP9T5D4PEX36CQ14";
    	String token="c6c27d12-1c54-4205-b257-a3281c77d3cf";
    	GameInstance gameInstance=createGame(levelId,token);
    	if (gameInstance!=null) {
    		gameInstanceString=gameInstance.getEntityId();
    		 
    		URI serverUrl = null;
			try {
				serverUrl = new URI("wss://goldrush.monad.fi/backend/"+token);
			} catch (URISyntaxException e) {
				
				e.printStackTrace();
			}
    		WebSocket client = new WebSocket(serverUrl);
	        client.connect();
	        CompletableFuture<Void> webSocketFuture = client.getWebSocketFuture();
         	 
         	 try {
                 webSocketFuture.get(); 
             } catch (Exception e) {
                 System.err.println("Error while waiting for WebSocket to close: " + e.getMessage());
             }
    		
        
    }
    }

	

}
