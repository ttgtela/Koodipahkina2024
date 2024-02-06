package com.tgtela.goldrush;
import com.google.gson.annotations.SerializedName;
public class RunCommandMessage {
    @SerializedName("run-command")
    private RunCommandPayload payload;

    public RunCommandMessage(RunCommandPayload payload) {
        this.payload = payload;
    }

    public RunCommandPayload getPayload() {
        return payload;
    }
}

class RunCommandPayload {
    private String gameId;
    private Object payload;

    public RunCommandPayload(String gameId, Object payload) {
        this.gameId = gameId;
        this.payload = payload;
    }

    public String getGameId() {
        return gameId;
    }

    public Object getPayload() {
        return payload;
    }
}