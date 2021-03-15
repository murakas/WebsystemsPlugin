//package qwebclient.controllers;
//
//import com.google.gson.*;
//
//import lombok.extern.log4j.Log4j2;
//import qwebclient.models.Resp;
//import ru.apertum.qsystem.common.*;
//import ru.apertum.qsystem.common.cmd.RpcGetSelfSituation;
//import ru.apertum.qsystem.common.exceptions.ClientException;
//import ru.apertum.qsystem.common.exceptions.QException;
//import ru.apertum.qsystem.common.model.INetProperty;
//import ru.apertum.qsystem.common.model.QCustomer;
//import ru.apertum.qsystem.hibernate.Dao;
//import ru.apertum.qsystem.server.model.QService;
//import ru.apertum.qsystem.server.model.QServiceTree;
//import ru.apertum.qsystem.server.model.QUser;
//import ru.apertum.qsystem.server.model.QUserList;
//import ru.apertum.qsystem.server.model.results.QResult;
//
//import javax.swing.tree.TreeNode;
//import javax.ws.rs.*;
//import javax.ws.rs.core.MediaType;
//import java.net.InetAddress;
//import java.net.UnknownHostException;
//import java.text.DateFormat;
//import java.text.SimpleDateFormat;
//import java.util.*;
//import java.util.List;
//
//import static ru.apertum.qsystem.common.QLog.log;
//import static ru.apertum.qsystem.server.model.QService.STATUS_FOR_USING;
//
//
///**
// * Тут пока все кроме параллельного приема
// */
//@Log4j2
//@Path("/client")
//public class ClientController {
//
//    /**
//     * Возможный состояния кнопок 1 - доступна кнопка, 0 - не доступна.
//     */
//
//    private static final String KEYS_OFF = "000000";
//    //private static final String KEYS_ALL = "111111";
//    private static final String KEYS_MAY_INVITE = "100000";
//    private static final String KEYS_INVITED = "111000";
//    private static final String KEYS_STARTED = "000111";
//    //private String keysCurrent = KEYS_OFF;
//    final DateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss:SSS", Locale.ENGLISH);
//    private final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
//
//
//    private static final INetProperty NET_PROPERTY = new INetProperty() {
//
//        @Override
//        public Integer getPort() {
//            return 3128;
//        }
//
//        @Override
//        public InetAddress getAddress() {
//            try {
//                return InetAddress.getByName("127.0.0.1");
//            } catch (UnknownHostException ex) {
//                throw new RuntimeException("Неверный адрес сервера: " + ex);
//            }
//        }
//    };
//
//    /**
//     * Список пользователей.
//     *
//     * @return Json
//     */
//    @GET
//    @Path("/getUsers")
//    @Produces(MediaType.APPLICATION_JSON)
//    public String getUsers() {
//        final long start = go();
//        log.info("WEB CLIENT API-[/getUsers]. Список пользователей.");
//        final LinkedList<QUser> users;
//        try {
//            users = NetCommander.getUsers(NET_PROPERTY);
//            end(start);
//            return GSON.toJson(new Resp(format.format(new Date()), 0, "ok", "Список пользователей", GSON.toJsonTree(users), "/getUsers"));
//        } catch (QException e) {
//            end(start);
//            return GSON.toJson(new Resp(format.format(new Date()), 1, "error", e.fillInStackTrace().toString(), null, "/getUsers"));
//        }
//    }
//
//    /**
//     * Проверить доступность учетки.
//     *
//     * @param userID id пользователя
//     * @return Json
//     */
//    @GET
//    @Path("/checkLocked")
//    @Produces(MediaType.TEXT_PLAIN)
//    public String checkLocked(@QueryParam("userID") long userID) {
//        final long start = go();
//        String res;
//        try {
//            res = !NetCommander.getSelfServicesCheck(NET_PROPERTY, userID) ? "Занято" : "Свободно";
//            end(start);
//            return GSON.toJson(new Resp(format.format(new Date()), 0, "ok", res, GSON.toJsonTree(res), "/checkLocked"));
//        } catch (QException e) {
//            end(start);
//            return GSON.toJson(new Resp(format.format(new Date()), 1, "error", e.fillInStackTrace().toString(), null, "/checkLocked"));
//        }
//    }
//
//
//    /**
//     * Связка учетки АИС Логистика с учеткой очереди.
//     *
//     * @param userID   id пользователя
//     * @param userUUID uuid пользоваталея АИС
//     * @return String
//     */
//    @GET
//    @Path("/logIn")
//    public String logIn(@QueryParam("userID") long userID, @QueryParam("userUUID") String userUUID) {
//        final long start = go();
//        Resp resp;
//        JsonElement situation;
//        final QUser user = QUserList.getInstance().getById(userID);
//        log.info("WEB CLIENT API-[/logIn]. Связываем пользователя АИС Логистика с окном.");
//        user.setUserUuid(userUUID);
//        final Exception execute = Dao.get().execute(() -> {
//            try {
//                Dao.get().saveOrUpdate(user);
//                log.debug("WEB CLIENT API-[/logIn]. Updated user_id of the user " + user.getName());
//            } catch (Exception ex) {
//                log.error("WEB CLIENT API-[/logIn]. Ошибка апдейта поля user_uuid " + user.getName() + "\n " + ex.toString());
//                return ex;
//            }
//            return null;
//        });
//        //вернем состояние
//        try {
//            situation = setSituation(NetCommander.getSelfServices(NET_PROPERTY, userID, true));
//        } catch (QException eq) {
//            situation = null;
//        }
//
//        resp = new Resp(format.format(new Date()), 0, "ok", "ok", situation, "/logIn");
//        if (execute != null) {
//            resp.setCode(1);
//            resp.setStatus("error");
//            resp.setMessage(execute.fillInStackTrace().getMessage());
//        }
//        end(start);
//
//        return GSON.toJson(resp);
//    }
//
//    /**
//     * Вызов следующего заявителя.
//     *
//     * @param userID id пользователя
//     * @return String возврщается последующее состояние очереди
//     */
//    @GET
//    @Path("/inviteNext")
//    @Produces(MediaType.TEXT_PLAIN)
//    public String inviteNext(@QueryParam("userID") long userID) {
//        final long start = go();
//        log.info("WEB CLIENT API-[/inviteNext]. Вызов следующего клиента.");
//        String message = null;
//        Resp resp;
//        JsonElement situation;
//        //получим кастомера
//        QCustomer customer;
//        try {
//            customer = NetCommander.inviteNextCustomer(NET_PROPERTY, userID);
//            if (customer != null) {
//                //только при первом вызове
//                if (CustomerState.STATE_INVITED.equals(customer.getState())) {
//                    //MDMLog.inviteNextCustomer(customer);
//                }
//                if (customer.getPostponPeriod() > 0) {
//                    message = "Посититель был отложен на " + customer.getPostponPeriod() + " мин.. И вызван со статусом \"" + customer.getPostponedStatus() + "\".";
//                }
//
//            }
//            //вернем состояние
//            situation = setSituation(NetCommander.getSelfServices(NET_PROPERTY, userID, true));
//            resp = new Resp(format.format(new Date()), 0, "ok", message, situation, "/inviteNext");
//        } catch (QException qe) {
//            resp = new Resp(format.format(new Date()), 1, "error", qe.getMessage(), null, "/inviteNext");
//        }
//        end(start);
//        return GSON.toJson(resp);
//    }
//
//
//    /**
//     * Вызов отложенного заявителя.
//     *
//     * @param userID     id пользователя очереди
//     * @param customerID id заявителя
//     * @return String
//     */
//    @GET
//    @Path("/invitePostponed")
//    @Produces(MediaType.TEXT_PLAIN)
//    public String invitePostponed(@QueryParam("userID") long userID, @QueryParam("customerID") long customerID) {
//        final long start = go();
//        log.info("WEB CLIENT API-[/invitePostponed].Вызов отложенного заявителя.");
//        Resp resp;
//        JsonElement situation;
//        try {
//            QCustomer customer = NetCommander.invitePostponeCustomer(NET_PROPERTY, userID, customerID);
//            //MDMLog.invitePostponeCustomer(customer);
//            //вернем состояние
//            situation = setSituation(NetCommander.getSelfServices(NET_PROPERTY, userID, true));
//            resp = new Resp(format.format(new Date()), 0, "ok", "Вызов отложенного заявителя", situation, "/invitePostponed");
//        } catch (QException eq) {
//            resp = new Resp(format.format(new Date()), 1, "error", eq.getMessage(), null, "/invitePostponed");
//        }
//        end(start);
//
//        return GSON.toJson(resp);
//    }
//
//    /**
//     * Вызов заявителя из определенной услуги.
//     *
//     * @param userID    id пользователя
//     * @param serviceID id услуг
//     * @return String
//     */
//    @GET
//    @Path("/inviteFlexCustomer")
//    @Produces(MediaType.TEXT_PLAIN)
//    public String inviteFlexCustomer(@QueryParam("userID") long userID, @QueryParam("serviceID") long serviceID) {
//        log.info("WEB CLIENT API-[/inviteFlexCustomer].Вызов заявителя из определенной услуги.");
//        final long start = go();
//        Resp resp;
//        JsonElement situation;
//        try {
//            QCustomer customer = NetCommander.inviteNextCustomer(NET_PROPERTY, userID, serviceID);
//            if (customer != null) {
//                //только при первом вызове
//                if (CustomerState.STATE_INVITED.equals(customer.getState())) {
//                    //.inviteNextCustomer(customer);
//                }
//            }
//            situation = setSituation(NetCommander.getSelfServices(NET_PROPERTY, userID, true));
//            resp = new Resp(format.format(new Date()), 0, "ok", "Вызов заявителя из определенной услуги", situation, "/inviteFlexCustomer");
//        } catch (QException qe) {
//            resp = new Resp(format.format(new Date()), 1, "error", qe.getMessage(), null, "/inviteFlexCustomer");
//        }
//        end(start);
//
//        return GSON.toJson(resp);
//    }
//
//    /**
//     * Начать работу с заявителем.
//     *
//     * @param userID id пользователя
//     * @return String
//     */
//    @GET
//    @Path("/startCustomer")
//    @Produces(MediaType.TEXT_PLAIN)
//    public String startCustomer(@QueryParam("userID") long userID) {
//        final long start = go();
//        log.info("WEB CLIENT API-[/startCustomer].Начать работу с заявителем.");
//        try {
//            RpcGetSelfSituation.SelfSituation selfSituation = NetCommander.getSelfServices(NET_PROPERTY, userID, true);
//            QCustomer customer = selfSituation.getCustomer() != null ? selfSituation.getCustomer() : null;
//            NetCommander.getStartCustomer(NET_PROPERTY, userID);
//            end(start);
//            //MDMLog.getStartCustomer(customer);
//            return GSON.toJson(new Resp(format.format(new Date()), 0, "ok", "Вызов заявителя из определенной услуги", GSON.toJsonTree(selfSituation), "/inviteFlexCustomer"));
//        } catch (QException qe) {
//            return "Ok";
//        }
//    }
//
//    /**
//     * Перед тем как удалить заявителя покажим необходимую информацию на окно.
//     *
//     * @param userID     id пользователя
//     * @param customerID id заявителя
//     * @return String
//     */
//    @GET
//    @Path("/beforeKillCustomer")
//    @Produces(MediaType.TEXT_PLAIN)
//    public String beforeKillCustomer(@QueryParam("userID") long userID, @QueryParam("customerID") long customerID) {
//        try {
//            log.info("WEB CLIENT API-[/beforeKillCustomer]. Перед тем как удалить заявителя покажим необходимую информацию на окно.");
//            RpcGetSelfSituation.SelfSituation selfSituation = NetCommander.getSelfServices(NET_PROPERTY, userID, true);
//            QCustomer customer = selfSituation.getCustomer() != null ? selfSituation.getCustomer() : null;
//            if (customer.getId().equals(customerID) && customer.getService().getExpectation() != 0 && (new Date().getTime() - customer.getStandTime().getTime()) / 1000 / 60 < customer.getService().getExpectation()) {
//                return String.format("Посетитель может задерживаться. Обязательное время ожидания не менее %s мин.", customer.getService().getExpectation()) +
//                        "\n" + "Вы действительно хотите удалить клиента из очереди?";
//            }
//            return "Вы действительно хотите удалить клиента из очереди?";
//        } catch (Exception e) {
//            log.error("WEB CLIENT API-[/beforeKillCustomer]. Ошибка при проверке перед тем как удалить заявителя");
//        }
//        return "Error";
//    }
//
//    /**
//     * Удалить заявителя из очереди.
//     *
//     * @param userID     id пользователя
//     * @param customerID id заявителя
//     * @return String
//     */
//    @GET
//    @Path("/killCustomer")
//    @Produces(MediaType.TEXT_PLAIN)
//    public String killCustomer(@QueryParam("userID") long userID, @QueryParam("customerID") long customerID) {
//        final long start = go();
//        Resp resp;
//        JsonElement situation;
//        try {
//            log.info("WEB CLIENT API-[/finishCustomer]. Удалить заявителя из очереди с id = " + customerID);
//            NetCommander.killNextCustomer(NET_PROPERTY, userID, customerID);
//            //вернем состояние
//            situation = setSituation(NetCommander.getSelfServices(NET_PROPERTY, userID, true));
//            resp = new Resp(format.format(new Date()), 0, "ok", "Удалить заявителя из очереди", situation, "/killCustomer");
//        } catch (QException eq) {
//            resp = new Resp(format.format(new Date()), 1, "error", eq.getMessage(), null, "/killCustomer");
//        }
//        end(start);
//
//        return GSON.toJson(resp);
//    }
//
//    /**
//     * Метод вызывается перед тем как мы хотим ПЕРЕНАПРАВИТЬ заявителя,
//     * он возвращает все необходимые данные для осуществления перенаправления.
//     *
//     * @param userID     id пользователя
//     * @param customerID id заявителя
//     * @return Json
//     */
//    @GET
//    @Path("/beforeRedirectCustomer")
//    @Produces(MediaType.APPLICATION_JSON)
//    public String beforeRedirectCustomer(@QueryParam("userID") long userID, @QueryParam("customerID") long customerID) throws QException {
//        log.info("WEB CLIENT API-[/beforeRedirectCustomer]. Получим данные для формы перед перенаправлением заявителя");
//
//        final JsonParser parser = new JsonParser();
//        final JsonObject jsonResult = new JsonObject();
//        final Gson gson = GsonPool.getInstance().borrowGson();
//        QCustomer customer;
//
//        //Получим текущую ситуацию
//        try {
//            RpcGetSelfSituation.SelfSituation selfSituation = NetCommander.getSelfServices(NET_PROPERTY, userID, true);
//            customer = selfSituation.getCustomer() != null ? selfSituation.getCustomer() : null;
//        } catch (Exception e) {
//            customer = null;
//        }
//
//        if (customer != null) {
//
//            //0 Обозначим результат если надо
//            final LinkedList<QResult> res;
//            if (customer.getService().getResultRequired()) {
//                res = NetCommander.getResultsList(NET_PROPERTY);
//                if (res != null) {
//                    jsonResult.addProperty("resRequired", true);
//                    jsonResult.add("res", parser.parse(gson.toJson(res)));
//                } else {
//                    return "Error. res is null";
//                }
//            }
//
//            //1 получить список услуг для редиректа
//            final List services = getServices();
//            if (services == null) {
//                //услуг нет выходим
//                return "Error. servicesList is null";
//            }
//            jsonResult.addProperty("servicesRequired", true);
//            jsonResult.add("services", parser.parse(gson.toJson(services)));
//
//            //2 определим нужно ли вернуть после заявителя
//            jsonResult.addProperty("requestBack", customer.needBack());
//
//            //3 скажем денусу что можно и коммент добавить при необходимости
//            jsonResult.addProperty("resComment", "");
//        }
//        return gson.toJson(jsonResult);
//    }
//
//
//    /**
//     * Перенаправляем заявителя, но прежде необходимо вызвать метод "/beforeRedirectCustomer"!
//     *
//     * @param userID      id пользователя
//     * @param customerID  id заявителя
//     * @param res         id возможного результата заврешения услуги (Обращение отработано/Невозможно отработать), список возращает "/beforeRedirectCustomer".
//     * @param serviceID   id услуги(очереди) куда перенаправялем заявителя, список возращает "/beforeRedirectCustomer".
//     * @param resComments комментарий по желанию пользователя (!!! Перед текстом обязательно имя пользователя. Пример: Окно 1: "Текст примечания").
//     * @param requestBack отметка о том нужно ли после обработки заявителя вернуть его туда откуда он пришел, значение возращает "/beforeRedirectCustomer".
//     * @return String
//     */
//    @GET
//    @Path("/redirectCustomer")
//    @Produces(MediaType.TEXT_PLAIN)
//    public String redirectCustomer(@QueryParam("userID") long userID,
//                                   @QueryParam("customerID") long customerID,
//                                   @QueryParam("serviceID") Long serviceID,
//                                   @QueryParam("requestBack") Boolean requestBack,
//                                   @QueryParam("resComments") String resComments,
//                                   @QueryParam("res") Long res) throws QException {
//        final long start = go();
//        log.info("WEB CLIENT API-[/redirectCustomer]. Перенаправить заявителя с id = " + customerID);
//        NetCommander.redirectCustomer(NET_PROPERTY, userID, customerID, serviceID, requestBack, resComments, res);
//        end(start);
//        return "Ok";
//    }
//
//
//    /**
//     * Метод вызывается перед тем как заврешить работу с заявителем.
//     *
//     * @param userID     id пользователя очереди
//     * @param customerID id заявителя очереди
//     * @return Json
//     */
//    @GET
//    @Path("/beforeFinishCustomer")
//    @Produces(MediaType.APPLICATION_JSON)
//    public String beforeFinishCustomer(@QueryParam("userID") long userID, @QueryParam("customerID") long customerID) throws QException {
//        final long start = go();
//        log.info("WEB CLIENT API-[/beforeFinishCustomer]. Получим данные для формы перед перенаправлением заявителя");
//        final JsonParser parser = new JsonParser();
//        final JsonObject jsonResult = new JsonObject();
//        final Gson gson = GsonPool.getInstance().borrowGson();
//
//        QCustomer customer;
//
//        //Получим текущую ситуацию
//        try {
//            RpcGetSelfSituation.SelfSituation selfSituation = NetCommander.getSelfServices(NET_PROPERTY, userID, true);
//            customer = selfSituation.getCustomer() != null ? selfSituation.getCustomer() : null;
//        } catch (Exception e) {
//            customer = null;
//        }
//
//        if (customer != null) {
//            //0 Обозначим результат если надо
//            final LinkedList<QResult> resultList;
//            if (customer.getService().getResultRequired()) {
//                resultList = NetCommander.getResultsList(NET_PROPERTY);
//                if (resultList != null) {
//                    jsonResult.addProperty("resRequired", true);
//                    jsonResult.add("res", parser.parse(gson.toJson(resultList)));
//                } else {
//                    return "Error. res is null";
//                }
//            } else {
//                jsonResult.addProperty("res", -1L);
//            }
//
//            //1 Диалог ввода коментария по кастомеру если он редиректенный и нужно его вернуть
//            if (customer.needBack()) {
//                jsonResult.addProperty("needBackComment", true);
//            }
//        }
//        end(start);
//
//        return gson.toJson(jsonResult);
//    }
//
//    /***
//     * Завершить работу с заявителем, но прежде необходимо вызвать метод "/beforeFinishCustomer".
//     * @param userID id пользоватлея
//     * @param customerID id заявителя
//     * @param res id из списка возможных результатов
//     * @param resComments комментарий от пользователя
//     * @return String
//     */
//
//    @GET
//    @Path("/finishCustomer")
//    @Produces(MediaType.TEXT_PLAIN)
//    public String finishCustomer(@QueryParam("userID") long userID,
//                                 @QueryParam("customerID") long customerID,
//                                 @QueryParam("res") Long res,
//                                 @QueryParam("resComments") String resComments) throws QException {
//        final QCustomer customer;
//        final QUser user;
//
//        final long start = go();
//        log.info("WEB CLIENT API-[/finishCustomer]. Завершить работу с заявителем с id = " + customerID);
//
//        //сохраним пользователя т.к. после пользователь будет недоступен
//        user = getUserFromId(userID);
//        customer = NetCommander.getFinishCustomer(NET_PROPERTY, userID, customerID, res, resComments);
//
//        //MDMLog.getFinishCustomer(customer, user);
//
//        end(start);
//        if (customer != null && customer.getService() != null && customer.getState() == CustomerState.STATE_WAIT_COMPLEX_SERVICE) {
//            return "Следующая услуга" + " \"" + customer.getService().getName() + "\". "
//                    + "Номер посетителя" + " \"" + customer.getFullNumber() + "\"." + "\n\n" + customer.getService().getDescription();
//        }
//        return "OK";
//    }
//
//    /**
//     * Получить текущую ситуацию.
//     *
//     * @param userID id пользователя очереди
//     * @return APPLICATION_JSON
//     */
//    @GET
//    @Path("/getSituation")
//    @Produces(MediaType.APPLICATION_JSON)
//    public String getSituation(@QueryParam("userID") long userID) {
//        String res;
//        final long start = go();
//        log.info("WEB CLIENT API-[/getSituation]. Получим текущую ситуацию для пользователя с id = " + userID);
//        try {
//            res = "";//setSituation(NetCommander.getSelfServices(NET_PROPERTY, userID, true));
//        } catch (Exception e) {
//            res = "Error";
//            log.error("WEB CLIENT API-[/getSituation]. Ошибка при получении текущей ситуации для пользователя с id = " + userID + ": " + e);
//        }
//        end(start);
//        return res;
//    }
//
//
//    /**
//     * Метод вызывается перед отправкой заявителя в отложенные.
//     *
//     * @return String or Json
//     */
//    @GET
//    @Path("/beforeMoveToPostponed")
//    @Produces(MediaType.TEXT_PLAIN)
//    public String beforeMoveToPostponed() {
//        final JsonParser parser = new JsonParser();
//        final JsonObject jsonResult = new JsonObject();
//        final Gson gson = GsonPool.getInstance().borrowGson();
//        LinkedList<QResult> res;
//        final HashMap<Integer, String> map = new HashMap<>();
//
//        map.put(0, "Бессрочно");
//        map.put(5, "5 минут");
//        map.put(10, "10 минут");
//        map.put(15, "15 минут");
//        map.put(20, "20 минут");
//        map.put(25, "25 минут");
//        map.put(30, "30 минут");
//
//        log.info("WEB CLIENT API-[/beforeMoveToPostponed]. Получим данные для формы перед отправкой заявителя в отложенные");
//        try {
//            res = NetCommander.getResultsList(NET_PROPERTY);
//        } catch (Exception e) {
//            res = null;
//            log.error("WEB CLIENT API-[/beforeMoveToPostponed]. Ошибка получения данных для формы перед отправкой заявителя в отложенные: " + e);
//        }
//
//        if (res != null) {
//            jsonResult.add("period", parser.parse(gson.toJson(map)));
//            jsonResult.add("res", parser.parse(gson.toJson(res)));
//            return gson.toJson(jsonResult);
//        } else {
//            return "Error";
//        }
//    }
//
//    /**
//     * Отправим в отложенные, но прежде необходимо вызвать "/beforeMoveToPostponed"
//     *
//     * @param userID          id пользоватлея
//     * @param customerID      id заявителя
//     * @param resName         Значение из списка возможных результатовя
//     * @param postponedPeriod время в минутах на которое отложили заявителя
//     * @param isMine          отметка о том что заявитель только мой
//     * @return String
//     */
//    @GET
//    @Path("/moveToPostponed")
//    @Produces(MediaType.TEXT_PLAIN)
//    public String moveToPostponed(@QueryParam("userID") long userID,
//                                  @QueryParam("customerID") long customerID,
//                                  @QueryParam("resName") String resName,
//                                  @DefaultValue("5") @QueryParam("postponedPeriod") Integer postponedPeriod,
//                                  @DefaultValue("false") @QueryParam("isMine") Boolean isMine) throws QException {
//
//
//        final long start = go();
//        log.info("WEB CLIENT API-[/moveToPostponed]. Отправим в отложенные заявителя с id = " + customerID);
//        try {
//            RpcGetSelfSituation.SelfSituation selfSituation = NetCommander.getSelfServices(NET_PROPERTY, userID, true);
//            QCustomer customer = selfSituation.getCustomer() != null ? selfSituation.getCustomer() : null;
//            //MDMLog.customerToPostpone(customer);
//        } catch (Exception ignored) {
//        }
//        NetCommander.customerToPostpone(NET_PROPERTY, userID, customerID, resName, postponedPeriod, isMine);
//        end(start);
//        return "OK";
//    }
//
//
//    /*
//     * ****************************************************************************************************************
//     * ******************************************* Вспомогательные методы *********************************************
//     * ****************************************************************************************************************
//     */
//
//
//    private long go() {
//        log().trace("WEB CLIENT API. Действие пользователя.");
//        return System.currentTimeMillis();
//    }
//
//    private void end(long start) {
//        log().trace("WEB CLIENT API. Действие завершено. Затрачено времени: " + ((double) (System.currentTimeMillis() - start)) / 1000 + " сек.\n");
//    }
//
//
//    private QUser getUserFromId(Long userID) throws QException {
//        LinkedList<QUser> list = NetCommander.getUsers(NET_PROPERTY);
//        if (!list.isEmpty()) {
//            return list.stream().filter(l -> (l.getId().equals(userID))).findFirst().get();
//        } else
//            return null;
//    }
//
//    /**
//     * Получить список доступных услуг.
//     */
//    private List getServices() throws QException {
//        final QService service = NetCommander.getServices(NET_PROPERTY).getRoot();
//        if (service == null) {
//            return null;
//        }
//        final LinkedList<QService> list = new LinkedList<>();
//        QServiceTree.sailToStorm(service, (TreeNode service1) -> {
//            final QService ser = (QService) service1;
//            if (ser.isRoot() || STATUS_FOR_USING.contains(ser.getStatus())) {
//                list.add(ser);
//            }
//        });
//        // дело в том, что из всего дерева услуг надо убрать заглушки и неактивные чтобы в них не перенаправлять.
//        // а дерево строится не по указанию на предка, а от корня по дочерним.
//        // вот и приходится удалять из дочерних.
//        list.forEach(ser -> {
//            final LinkedList<QService> del = new LinkedList<>();
//            ser.getChildren().stream().filter(che -> (!STATUS_FOR_USING.contains(che.getStatus()))).forEach(del::add);
//            del.forEach(ser::remove);
//        });
//        return list;
//    }
//
//    /**
//     * Определяет какова ситуация в очереди к пользователю.
//     *
//     * @param plan - ситуация в XML
//     * @return String
//     */
//
//    private JsonObject setSituation(RpcGetSelfSituation.SelfSituation plan) {
//        JsonObject jsonRes;
//        JsonParser parser = new JsonParser();
//        Gson gson = new GsonBuilder().create();
//
//        String buttonsState = KEYS_OFF;
//
//        if (plan.getCustomer() != null) {
//
//            QCustomer customer = plan.getCustomer();
//            String keyStarted = KEYS_STARTED.substring(0, KEYS_STARTED.length() - 1) + (customer.getService().getEnable() == 1 ? 1 : 0);
//
//            switch (customer.getState()) {
//                case STATE_INVITED:
//                case STATE_INVITED_SECONDARY: {
//                    buttonsState = KEYS_INVITED;
//                    break;
//                }
//                case STATE_WORK:
//                case STATE_WORK_SECONDARY: {
//                    buttonsState = keyStarted;
//                    break;
//                }
//                default: {
//                    throw new ClientException("Не известное состояние клиента \"" + customer.getState() + "\" для данного случая.");
//                }
//            }
//        }
//        int inCount = 0;
//
//        for (RpcGetSelfSituation.SelfService service : plan.getSelfservices()) {
//            inCount = inCount + service.getCountWait();
//        }
//
//        if (plan.getCustomer() != null) {
//            System.out.println("От сервера приехал кастомер, который обрабатывается юзером.");
//        } else {
//            if (inCount == 0) {
//                buttonsState = KEYS_OFF;
//                //* нет клиентов, нечеого вызывать
//            } else {
//                buttonsState = KEYS_MAY_INVITE;
//                //*в очереди кто-то есть, можно вызвать
//            }
//            //нефиг счелкать касторами пока процесс вызывания идет при параллельном приеме
//            //но сейчас кастомера вообще нет, атк что можно щелкать
//            /*if (user.getParallelAccess()) {
//                listParallelClients.setEnabled(true);
//            }*/
//        }
//
//        jsonRes = parser.parse(gson.toJson(plan)).getAsJsonObject();
//        jsonRes.addProperty("buttonsState", buttonsState);
//        return jsonRes;
//    }
//
//}
