package webSocketMessages.serverMessages;

//public class Error extends ServerMessage{
//    private String errorMessage;
//    public Error(String errorMessage) {
//        super(ServerMessageType.ERROR);
//        this.errorMessage = errorMessage;
//    }
//    public String getMessage() {
//        return errorMessage;
//    }
//}

public class Error extends ServerMessage {

    String errorMessage;

    public Error(ServerMessageType serverMessageType, String message) {
        super(serverMessageType);
        this.errorMessage = message;
    }

    public String getMessage() {
        return this.errorMessage;
    }
}
