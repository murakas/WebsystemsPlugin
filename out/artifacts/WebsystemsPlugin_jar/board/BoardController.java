package websystems.board;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.dom4j.Element;
import ru.apertum.qsystem.client.forms.AFBoardRedactor;
import ru.apertum.qsystem.common.CustomerState;
import ru.apertum.qsystem.common.model.QCustomer;
import ru.apertum.qsystem.server.controller.IIndicatorBoard;
import ru.apertum.qsystem.server.model.QService;
import ru.apertum.qsystem.server.model.QServiceTree;
import ru.apertum.qsystem.server.model.QUser;
import ru.apertum.qsystem.server.model.QUserList;
import websystems.utils.WSServer;

import javax.swing.*;
import java.util.LinkedList;
import java.util.PriorityQueue;

public class BoardController implements IIndicatorBoard {

    private static final Gson GSON = new Gson();
    private static final String BLINK = "blink";
    private static WSServer wsserver = new WSServer(8881);
    private static String cmd = "firefox.exe -kiosk /board/content.html";
    private static Runtime run = Runtime.getRuntime();
    private static Process pr;

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
        wsserver.broadcast(prepareContent());
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
        sendContent();
    }

    @Override
    public void showBoard() {
        wsserver.start();
        try {
            Thread.sleep(3000);
            pr = run.exec(cmd);
            pr.waitFor();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
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

    public static void main(String[] args) {
        try {
            Thread.sleep(3000);
            pr = run.exec(cmd);
            pr.waitFor();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }
}
