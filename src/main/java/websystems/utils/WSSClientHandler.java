package websystems.utils;

import com.google.gson.Gson;

public class WSSClientHandler {

    static class SocketResponse {
        private String command;
        private String value;

        public SocketResponse(String command, String value) {
            this.command = command;
            this.value = value;
        }

        public String getCommand() {
            return command;
        }

        public void setCommand(String command) {
            this.command = command;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    private static final Gson GSON = new Gson();
    private static WSServer wsServer = new WSServer(8009);


    public WSSClientHandler(Integer port) {
    }

    public static void start() {
        wsServer.start();
    }

    private static void sendBroadcastMessage(String message) {
        if (wsServer != null) {
            wsServer.broadcast(message);
        }
    }

    public static void resetAuthorization(long userId) {
        sendBroadcastMessage(GSON.toJson(new SocketResponse("resetAuthorization", String.valueOf(userId))));
    }

    public static void newCustomer(String message) {
        sendBroadcastMessage(GSON.toJson(new SocketResponse("updateSituation", message)));
    }
}
