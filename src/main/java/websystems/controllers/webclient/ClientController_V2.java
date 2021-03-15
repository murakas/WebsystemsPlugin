package websystems.controllers.webclient;

import com.google.gson.*;
import lombok.extern.log4j.Log4j2;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.dialect.MySQLDialect;
import ru.apertum.qsystem.hibernate.ChangeServerAction;
import websystems.controllers.mdm.MdmController;
import websystems.models.webclient.ResponseClient;
import ru.apertum.qsystem.common.NetCommander;
import ru.apertum.qsystem.common.cmd.RpcGetSelfSituation;
import ru.apertum.qsystem.common.exceptions.ClientException;
import ru.apertum.qsystem.common.exceptions.QException;
import ru.apertum.qsystem.common.model.INetProperty;
import ru.apertum.qsystem.common.model.QCustomer;
import ru.apertum.qsystem.server.model.QUser;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;

//2-ая версия
@Log4j2
@Path("/api/client")
public class ClientController_V2 {
    private static final String KEYS_OFF = "000000";
    //private static final String KEYS_ALL = "111111";
    private static final String KEYS_MAY_INVITE = "100000";
    private static final String KEYS_INVITED = "111000";
    private static final String KEYS_STARTED = "000111";
    //private String keysCurrent = KEYS_OFF;
    final DateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss:SSS", Locale.ENGLISH);
    private final Gson GSON = new GsonBuilder().setPrettyPrinting().create();


    private static final INetProperty NET_PROPERTY = new INetProperty() {

        @Override
        public Integer getPort() {
            return 3128;
        }

        @Override
        public InetAddress getAddress() {
            try {
                return InetAddress.getByName("127.0.0.1");
            } catch (UnknownHostException ex) {
                throw new RuntimeException("Неверный адрес сервера: " + ex);
            }
        }
    };

    /**
     * Связка учетки АИС Логистика с учеткой очереди.
     *
     * @param userId   id пользователя
     * @param userUuid uuid пользоваталея АИС
     * @return json
     */
    @POST
    @Path("/logIn")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String logIn(@FormParam("userId") long userId, @FormParam("userUuid") String userUuid) {
        if (userUuid == null || userUuid.isEmpty()) return
                GSON.toJson(new ResponseClient(format.format(new Date()), 1, "error", "Параметр userUuid не может быть пустым", null, "/logIn"));
        ResponseClient responseClient;

        final long start = go();
        log.info("[/logIn] Связываем пользователя АИС Логистика с окном путем обновления поля userUuid = " + userUuid + " пользователю c id=" + userId);
        try {
            ChangeServerAction config = new ChangeServerAction();

            Class.forName(config.getDriver().contains("mysql") ? MySQLDialect.class.getName() : H2Dialect.class.getName());

            Connection conn = DriverManager.getConnection(config.getUrl(), config.getUser(), config.getParolcheg());

            PreparedStatement preparedStmt = conn.prepareStatement("update users set user_uuid = ? where id = ?");
            preparedStmt.setString(1, userUuid);
            preparedStmt.setLong(2, userId);
            preparedStmt.executeUpdate();
            preparedStmt.close();

            conn.close();
            responseClient = new ResponseClient(format.format(new Date()), 0, "ok", "Успешно связан " + userId + " с " + userUuid, null, "/logIn");
        } catch (Exception e) {
            responseClient = new ResponseClient(format.format(new Date()), 1, "error", e.getMessage(), null, "/logIn");
        }
        end(start);

        return GSON.toJson(responseClient);
    }

    /**
     * Проверить доступность учетки.
     *
     * @param userId id пользователя
     * @return Json
     */
    @POST
    @Path("/checkLocked")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String checkLocked(@FormParam("userId") long userId) {
        final long start = go();
        log.info("[/checkLocked] Проверить доступность учетки");
        ResponseClient responseClient;
        try {
            String res = !NetCommander.getSelfServicesCheck(NET_PROPERTY, userId) ? "Занято" : "Свободно";
            responseClient = new ResponseClient(format.format(new Date()), 0, "ok", res, GSON.toJsonTree(res), "/checkLocked");
        } catch (QException e) {
            log.error("[/checkLocked] Ошибка проверки доступности учетки", e);
            responseClient = new ResponseClient(format.format(new Date()), 1, "error", e.fillInStackTrace().toString(), null, "/checkLocked");
        }

        end(start);
        return GSON.toJson(responseClient);
    }

    /**
     * Список пользователей.
     *
     * @return json
     */
    @GET
    @Path("/getUsers")
    @Produces(MediaType.APPLICATION_JSON)
    public String getUsers() {
        final long start = go();
        log.info("[/getUsers] Получить список пользователей");
        ResponseClient responseClient;
        final LinkedList<QUser> users;
        try {
            users = NetCommander.getUsers(NET_PROPERTY);
            responseClient = new ResponseClient(format.format(new Date()), 0, "ok", "Список пользователей", GSON.toJsonTree(users), "/getUsers");
        } catch (QException e) {
            log.error("[/getUsers] Ошибка получения списока пользователей", e);
            responseClient = new ResponseClient(format.format(new Date()), 1, "error", e.getMessage(), null, "/getUsers");
        }
        end(start);
        return GSON.toJson(responseClient);
    }

    /**
     * Вызов следующего клиента.
     *
     * @param userId id пользователя
     * @return json
     */
    @POST
    @Path("/inviteNext")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String inviteNext(@FormParam("userId") long userId) {
        final long start = go();
        log.info("[/inviteNext] Вызов следующего клиента");
        ResponseClient responseClient;
        //получим кастомера
        QCustomer customer;
        try {
            customer = NetCommander.inviteNextCustomer(NET_PROPERTY, userId);
//            if (customer != null) {
//                //только при первом вызове
//                if (CustomerState.STATE_INVITED.equals(customer.getState())) {
//                    MdmController.inviteNextCustomer(customer);
//                }
//            }
            responseClient = new ResponseClient(format.format(new Date()), 0, "ok", "Вызван клиент " + customer.getFullNumber(), GSON.toJsonTree(customer), "/inviteNext");
        } catch (Throwable th) {
            log.error("[/inviteNext] Ошибка вызова следующего клиента", th);
            responseClient = new ResponseClient(format.format(new Date()), 1, "error", th.getMessage(), null, "/inviteNext");
        }

        end(start);
        return GSON.toJson(responseClient);
    }

    /**
     * Вызов следующего клиента из определнной услуги.
     *
     * @param userId    id пользователя
     * @param serviceId id услуги
     * @return json
     */
    @POST
    @Path("/inviteFlexNext")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String inviteFlexNext(@FormParam("userId") long userId, @FormParam("serviceId") long serviceId) {
        final long start = go();
        ResponseClient responseClient;
        log.info("[/inviteFlexNext] Вызов следующего клиента из услуги " + serviceId);
        try {
            final QCustomer customer = NetCommander.inviteNextCustomer(NET_PROPERTY, userId, serviceId);
            responseClient = new ResponseClient(format.format(new Date()), 0, "ok", "Вызван клиент " + customer.getFullNumber(), GSON.toJsonTree(customer), "/inviteFlexNext");
        } catch (Throwable th) {
            log.error("[/inviteFlexNext] Ошибка вызова следующего клиента из усулги " + serviceId, th);
            responseClient = new ResponseClient(format.format(new Date()), 1, "error", th.getMessage(), null, "/inviteFlexNext");
        }
        end(start);
        return GSON.toJson(responseClient);
    }

    /**
     * Начать работу с клиентом.
     *
     * @param userId id пользователя
     * @return json
     */
    @POST
    @Path("/startCustomer")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String startCustomer(@FormParam("userId") long userId) {
        final long start = go();
        ResponseClient responseClient;
        log.info("[/startCustomer] Начать работу с клиентом");
        try {
            NetCommander.getStartCustomer(NET_PROPERTY, userId);
            //MDMLog.getStartCustomer(customer);
            responseClient = new ResponseClient(format.format(new Date()), 0, "ok", "Начал работу с клиентом", null, "/startCustomer");
        } catch (Throwable e) {
            log.error("[/startCustomer] Ошибка работы с клиентом", e);
            responseClient = new ResponseClient(format.format(new Date()), 1, "error", e.getMessage(), null, "/startCustomer");
        }
        end(start);
        return GSON.toJson(responseClient);
    }

    /**
     * Удалить клиента из очереди.
     *
     * @param userId     id пользователя
     * @param customerId id клиента
     * @return json
     */
    @POST
    @Path("/killCustomer")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String killCustomer(@FormParam("userId") long userId, @FormParam("customerId") long customerId) {
        final long start = go();
        ResponseClient responseClient;
        log.info("[/killCustomer] Удалить клиента из очереди с id = " + customerId);
        try {
            NetCommander.killNextCustomer(NET_PROPERTY, userId, customerId);
            responseClient = new ResponseClient(format.format(new Date()), 0, "ok", "Клиент успешно удален", null, "/killCustomer");
        } catch (Throwable e) {
            log.error("[/killCustomer] Ошибка удаления клиента из очереди с id = " + customerId, e);
            responseClient = new ResponseClient(format.format(new Date()), 1, "error", e.getMessage(), null, "/killCustomer");
        }
        end(start);

        return GSON.toJson(responseClient);
    }

    /**
     * Завершить прием.
     *
     * @param userId      id пользователя
     * @param customerId  id клиент
     * @param res         код результата оказания услуги
     * @param resComments комментарий
     * @return json
     */
    @POST
    @Path("/finishCustomer")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String finishCustomer(@FormParam("userId") long userId,
                                 @FormParam("customerId") long customerId,
                                 @FormParam("customerStartTime") long customerStartTime,
                                 @FormParam("res") long res,
                                 @FormParam("resComments") String resComments) {
        final long start = go();
        //QCustomer customer1 = GSON.fromJson(sourceCustomer, QCustomer.class);
        ResponseClient responseClient;
        log.info("[/finishCustomer] Завершить прием клиента id = " + customerId);
        QCustomer customer = null;
        try {
            customer = NetCommander.getFinishCustomer(NET_PROPERTY, userId, customerId, res, resComments);
            responseClient = new ResponseClient(format.format(new Date()), 0, "ok", "Работа с клиентом " + customer.getFullNumber() + " успешна завершена", GSON.toJsonTree(customer), "/finishCustomer");
        } catch (Throwable e) {
            log.error("[/finishCustomer] Ошибка завершения прием клиента id = " + customerId, e);
            responseClient = new ResponseClient(format.format(new Date()), 1, "error", e.getMessage(), null, "/finishCustomer");
        }

        if (customer != null) {
            final QCustomer threadCustomer = customer;
            threadCustomer.setStartTime(new Date(customerStartTime));
            Runnable task = () -> MdmController.save_4_2_3_34_mdmObjects(threadCustomer, userId);
            Thread thread = new Thread(task);
            thread.start();

        }
        end(start);

        return GSON.toJson(responseClient);
    }

    /**
     * Изменить статус отложеному.
     *
     * @param customerId id клиента
     * @param status     статус
     * @return json
     */
    @POST
    @Path("/changeStatusForPostponed")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String changeStatusForPostponed(@FormParam("customerId") long customerId, @FormParam("status") String status) {
        final long start = go();
        ResponseClient responseClient;
        log.info("[/changeStatusForPostponed] Изменить статус отложеному клиенту " + customerId);
        try {
            NetCommander.postponeCustomerChangeStatus(NET_PROPERTY, customerId, status);
            responseClient = new ResponseClient(format.format(new Date()), 0, "ok", "Статус успешно изменен", null, "/changeStatusForPostponed");
        } catch (Throwable e) {
            log.error("[/changeStatusForPostponed] Ошибка при измении статуса отложеному клиенту " + customerId, e);
            responseClient = new ResponseClient(format.format(new Date()), 1, "error", e.getMessage(), null, "/changeStatusForPostponed");
        }
        end(start);

        return GSON.toJson(responseClient);
    }

    /**
     * Отправим в отложенные.
     *
     * @param userId     id пользователя
     * @param customerId id клиента
     * @param result     результат
     * @param period     переиод на который был отложен
     * @param isMine     только мой клиент
     * @return json
     */
    @POST
    @Path("/moveToPostponed")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String moveToPostponed(@FormParam("userId") long userId,
                                  @FormParam("customerId") long customerId,
                                  @FormParam("result") String result,
                                  @FormParam("period") int period,
                                  @FormParam("isMine") boolean isMine) {
        final long start = go();
        ResponseClient responseClient;
        log.info("[/moveToPostponed] Изменить статус отложеному клиенту " + customerId);
        try {
            NetCommander.customerToPostpone(NET_PROPERTY, userId, customerId, result, period, isMine);
            responseClient = new ResponseClient(format.format(new Date()), 0, "ok", "Успешно переведен в отложенные", null, "/moveToPostponed");
        } catch (Throwable e) {
            log.error("[/moveToPostponed] Ошибка при измении статуса отложеному клиенту " + customerId, e);
            responseClient = new ResponseClient(format.format(new Date()), 1, "error", e.getMessage(), null, "/moveToPostponed");
        }
        end(start);

        return GSON.toJson(responseClient);
    }


    /**
     * Вызовем отложенного.
     *
     * @param userId     id пользователя
     * @param customerId id клиента
     * @return json
     */
    @POST
    @Path("/invitePostponed")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String invitePostponed(@FormParam("userId") long userId, @FormParam("customerId") long customerId) {
        final long start = go();
        ResponseClient responseClient;
        log.info("[/invitePostponed] Вызвать отложенного клиента " + customerId);
        try {
            QCustomer customer = NetCommander.invitePostponeCustomer(NET_PROPERTY, userId, customerId);
            responseClient = new ResponseClient(format.format(new Date()), 0, "ok", "Успешно вызван", GSON.toJsonTree(customer), "/invitePostponed");
        } catch (Throwable e) {
            log.error("[/invitePostponed] Ошибка вызова отложенного клиента " + customerId, e);
            responseClient = new ResponseClient(format.format(new Date()), 1, "error", e.getMessage(), null, "/invitePostponed");
        }
        end(start);
        return GSON.toJson(responseClient);
    }

    /**
     * Действие по нажатию кнопки "Перенаправить".
     *
     * @param userId       id пользователя
     * @param customerId   id клиента
     * @param res          код результата
     * @param serviceId    id услуги
     * @param requestBack  после вернуть?
     * @param userName     имя пользователя
     * @param tempComments комментарий
     * @return json
     */
    @POST
    @Path("/redirectCustomer")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String redirectCustomer(@FormParam("userId") long userId,
                                   @FormParam("customerId") long customerId,
                                   @FormParam("res") long res,
                                   @FormParam("serviceId") long serviceId,
                                   @FormParam("requestBack") boolean requestBack,
                                   @FormParam("userName") String userName,
                                   @FormParam("tempComments") String tempComments) {
        final long start = go();
        ResponseClient responseClient;
        log.info("[/redirectCustomer] Перенаправление клиента " + customerId + " в другую услугу " + serviceId);
        try {
            String comment = "";
            if (tempComments != null && !tempComments.isEmpty()) {
                comment = userName + ": " + tempComments;
            }
            NetCommander.redirectCustomer(NET_PROPERTY, userId, customerId, serviceId, requestBack, comment, res);

            responseClient = new ResponseClient(format.format(new Date()), 0, "ok", "Успешно перенаправлен", null, "/redirectCustomer");
        } catch (Throwable e) {
            log.error("[/redirectCustomer] Ошибка перенаправления клиента " + customerId + " в другую услугу " + serviceId, e);
            responseClient = new ResponseClient(format.format(new Date()), 1, "error", e.getMessage(), null, "/redirectCustomer");
        }
        end(start);

        return GSON.toJson(responseClient);
    }

    /**
     * Получить текущую ситуацию.
     *
     * @param userId id пользователя очереди
     * @return json
     */
    @POST
    @Path("/getSituation")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String getSituation(@FormParam("userId") long userId) {
        final long start = go();
        JsonObject situation;
        ResponseClient responseClient;
        //log.info("[/getSituation] Получим текущую ситуацию для пользователя " + userId);
        try {
            situation = setSituation(NetCommander.getSelfServices(NET_PROPERTY, userId, true));
            responseClient = new ResponseClient(format.format(new Date()), 0, "ok", "Текущее состояние очереди", situation, "/getSituation");
        } catch (Throwable e) {
            log.error("[/getSituation] Ошибка при получении текущей ситуации для пользователя" + userId, e);
            responseClient = new ResponseClient(format.format(new Date()), 1, "error", e.getMessage(), null, "/getSituation");
        }
        end(start);
        return GSON.toJson(responseClient);
    }


    /*
     * ****************************************************************************************************************
     * ******************************************* Вспомогательные методы *********************************************
     * ****************************************************************************************************************
     */

//    public class NotAuthorizedException extends WebApplicationException {
//        public NotAuthorizedException(String message) {
//            super(Response.status(Response.Status.UNAUTHORIZED)
//                    .entity(message).type(MediaType.TEXT_PLAIN).build());
//        }
//    }

    private long go() {
        log.trace("Действие пользователя");
        return System.currentTimeMillis();
    }

    private void end(long start) {
        log.trace("Действие завершено. Затрачено времени: " + ((double) (System.currentTimeMillis() - start)) / 1000 + " сек.");
    }


    private QUser getUserFromId(Long userID) throws QException {
        LinkedList<QUser> list = NetCommander.getUsers(NET_PROPERTY);
        if (!list.isEmpty()) {
            return list.stream().filter(l -> (l.getId().equals(userID))).findFirst().get();
        } else
            return null;
    }

    /**
     * Определяет какова ситуация в очереди к пользователю.
     *
     * @param plan - ситуация в XML
     * @return json
     */

    private JsonObject setSituation(RpcGetSelfSituation.SelfSituation plan) {
        JsonObject jsonRes;
        JsonParser parser = new JsonParser();
        Gson gson = new GsonBuilder().create();

        String buttonsState = KEYS_OFF;

        if (plan.getCustomer() != null) {

            QCustomer customer = plan.getCustomer();
            String keyStarted = KEYS_STARTED.substring(0, KEYS_STARTED.length() - 1) + (customer.getService().getEnable() == 1 ? 1 : 0);

            switch (customer.getState()) {
                case STATE_INVITED:
                case STATE_INVITED_SECONDARY: {
                    buttonsState = KEYS_INVITED;
                    break;
                }
                case STATE_WORK:
                case STATE_WORK_SECONDARY: {
                    buttonsState = keyStarted;
                    break;
                }
                default: {
                    throw new ClientException("Не известное состояние клиента \"" + customer.getState() + "\" для данного случая.");
                }
            }
        }
        int inCount = 0;

        for (RpcGetSelfSituation.SelfService service : plan.getSelfservices()) {
            inCount = inCount + service.getCountWait();
        }

        if (plan.getCustomer() != null) {
            System.out.println("От сервера приехал кастомер, который обрабатывается юзером.");
        } else {
            if (inCount == 0) {
                buttonsState = KEYS_OFF;
                //* нет клиентов, нечеого вызывать
            } else {
                buttonsState = KEYS_MAY_INVITE;
                //*в очереди кто-то есть, можно вызвать
            }
            //нефиг счелкать касторами пока процесс вызывания идет при параллельном приеме
            //но сейчас кастомера вообще нет, атк что можно щелкать
            /*if (user.getParallelAccess()) {
                listParallelClients.setEnabled(true);
            }*/
        }

        jsonRes = parser.parse(gson.toJson(plan)).getAsJsonObject();
        jsonRes.addProperty("buttonsState", buttonsState);
        return jsonRes;
    }
}
