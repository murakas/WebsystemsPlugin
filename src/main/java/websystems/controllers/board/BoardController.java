package websystems.controllers.board;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.dom4j.Element;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import ru.apertum.qsystem.client.forms.AFBoardRedactor;
import ru.apertum.qsystem.common.CustomerState;
import ru.apertum.qsystem.common.model.QCustomer;
import ru.apertum.qsystem.server.controller.IIndicatorBoard;
import ru.apertum.qsystem.server.model.QService;
import ru.apertum.qsystem.server.model.QServiceTree;
import ru.apertum.qsystem.server.model.QUser;
import ru.apertum.qsystem.server.model.QUserList;

import javax.swing.*;
import java.net.InetSocketAddress;
import java.util.LinkedList;
import java.util.PriorityQueue;

public class BoardController implements IIndicatorBoard {

    private final Gson GSON = new Gson();
    private final String BLINK = "blink";
    private WSSBoard wssBoard = new WSSBoard(8881);
    private String cmd = "firefox.exe -kiosk file://";
    private Runtime run = Runtime.getRuntime();
    private Process pr;
    private String fullPath = BoardController.class.getProtectionDomain().getCodeSource().getLocation().getPath();
    private final String PATH = fullPath.substring(0, fullPath.lastIndexOf("/"));

    class WSSBoard extends WebSocketServer {

        public WSSBoard(int port) {
            super(new InetSocketAddress(port));
        }

        @Override
        public void onOpen(WebSocket conn, ClientHandshake handshake) {
            broadcast(prepareContent());
        }

        @Override
        public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        }

        @Override
        public void onMessage(WebSocket conn, String message) {
        }

        @Override
        public void onError(WebSocket conn, Exception ex) {
        }

        @Override
        public void onStart() {
            runFirefox();
        }
    }

    public BoardController() {
        init();
    }

    private void init() {
        wssBoard.start();
    }

    /**
     * Сбор данных для передачи.
     */
    private String prepareContent() {
        final JsonObject jsonObject = new JsonObject();
        // Построем всех ближайших
        prepareNext(jsonObject);

        // Нужно найти всех вызванных и обрабатываемых, посмотреть их статус и заменить данные и id для мигания вызванных
        prepareInvited(jsonObject);

        return GSON.toJson(jsonObject);
    }

    private void prepareNext(JsonObject jsonObject) {
        final LinkedList<String> nexts = new LinkedList<>(); // Это все ближайшие по порядку
        final PriorityQueue<QCustomer> customers = new PriorityQueue<>();
        QServiceTree.getInstance().getNodes().stream().filter(QService::isLeaf).forEach(service ->
                service.getClients().stream().forEach(customers::add)
        );
        QCustomer customer = customers.poll();
        while (customer != null) {
            nexts.add(customer.getFullNumber());
            customer = customers.poll();
        }
        jsonObject.add("nexts", new Gson().toJsonTree(nexts));
    }

    private void prepareInvited(JsonObject jsonObject) {
        final LinkedList<QCustomer> onBoard = new LinkedList<>();
        QUserList.getInstance().getItems().stream().filter((user) -> (user.getCustomer() != null)).forEachOrdered((user) -> {
            onBoard.add(user.getCustomer());
        });

        // Заменяем строки вызванных
        JsonArray jsonArray = new JsonArray();
        for (QCustomer customer : onBoard) {
            JsonObject jsonBoard = new JsonObject();
            jsonBoard.addProperty("number", customer.getFullNumber());
            jsonBoard.addProperty("point", customer.getUser().getPoint());
            //jsonBoard.addProperty("ext", customer.getUser().getPointExt());
            if (customer.getState().equals(CustomerState.STATE_INVITED) || customer.getState().equals(CustomerState.STATE_INVITED_SECONDARY)) {
                jsonBoard.addProperty(BLINK, "true");
            } else {
                jsonBoard.addProperty(BLINK, "false");
            }
            jsonArray.add(jsonBoard);
        }
        jsonObject.add("onBoard", jsonArray);
    }

    private void sendContent() {
        wssBoard.broadcast(prepareContent());
    }

    @Override
    public void customerStandIn(QCustomer customer) {
        sendContent();
    }

    @Override
    public void inviteCustomer(QUser qUser, QCustomer qCustomer) {
        sendContent();
    }

    @Override
    public void workCustomer(QUser qUser) {
        sendContent();
    }

    @Override
    public void killCustomer(QUser qUser) {
        sendContent();
    }

    @Override
    public void close() {
        if (pr != null && pr.isAlive()) pr.destroy();
    }

    @Override
    public void refresh() {
        if (pr != null && pr.isAlive()) pr.destroy();
        runFirefox();
        sendContent();
    }

    @Override
    public void showBoard() {
        if (pr != null && pr.isAlive()) pr.destroy();
        runFirefox();
        sendContent();
    }

    @Override
    public void clear() {
        sendContent();
    }

    @Override
    public void setConfigFile(String s) {
    }

    @Override
    public Element getConfig() {
        return null;
    }

    @Override
    public void saveConfig(Element element) {

    }

    @Override
    public AFBoardRedactor getRedactor() {
        return null;
    }

    @Override
    public Object getBoardForm() {
        return null;
    }

    @Override
    public String getDescription() {
        return "Табло для МФЦ";
    }

    @Override
    public long getUID() {
        return 1363l;
    }

    private void runFirefox() {
        try {
            pr = run.exec(cmd + PATH + "/board/content.html");
            pr.waitFor();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }
}
