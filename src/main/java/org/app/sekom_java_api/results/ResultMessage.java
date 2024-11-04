package org.app.sekom_java_api.results;

public class ResultMessage {

    public final ResultMessageType messageType;

    public final String messageText;

    public ResultMessage(ResultMessageType messageType, String messageText){
        this.messageText = messageText;
        this.messageType = messageType;
    }
}
