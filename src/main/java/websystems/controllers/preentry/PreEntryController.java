package websystems.controllers.preentry;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.extern.log4j.Log4j2;
import ru.apertum.qsystem.common.GsonPool;
import ru.apertum.qsystem.common.NetCommander;
import ru.apertum.qsystem.common.cmd.CmdParams;
import ru.apertum.qsystem.common.cmd.RpcGetBool;
import ru.apertum.qsystem.common.cmd.RpcGetGridOfWeek;
import ru.apertum.qsystem.common.model.INetProperty;
import ru.apertum.qsystem.server.model.QService;
import ru.apertum.qsystem.server.model.QServiceTree;
import websystems.domains.preentry.Customer;
import websystems.domains.preentry.PreEntry;
import websystems.domains.preentry.Service;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Log4j2
@Path("/api/preentry")
public class PreEntryController implements IPreEntry {

    private final Gson GSON = new Gson();
    private final DateFormat HUMAN_FORMAT = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.ENGLISH);

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

    @GET
    @Path("/getServiceList")
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public String getServiceList() {
        final QService root;
        final List<Service> list = new ArrayList<>();
        try {
            root = NetCommander.getServices(NET_PROPERTY).getRoot();
            QServiceTree.sailToStorm(root, service -> {
                if (service.isLeaf() && ((QService) service).getAdvanceLimit() != 0 && (((QService) service).getStatus() == 1 || ((QService) service).getStatus() == 2)) {
                    QService qs = (QService) service;
                    list.add(new Service(qs.getId(), qs.getName()));
                }
            });

            return GSON.toJson(getJsonResult(
                    0,
                    "ok",
                    "Список Услуг для " + root.getName(),
                    GSON.toJsonTree(list),
                    "/getServices"));
        } catch (Exception e) {
            return GSON.toJson(getJsonResult(
                    1,
                    "error",
                    "Произошла ошибка:" + e.getMessage(),
                    null,
                    "/getServices"));
        }
    }

    @GET
    @Path("/getGridOfWeek")
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public String getGridOfWeek(@QueryParam("servicesId") Long serviceId, @QueryParam("date") String date, @QueryParam("mfcName") String mfcName) {

        final List list = new ArrayList();
        final DateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        final Date startDate;

        try {
            startDate = simpleFormat.parse(date);
        } catch (ParseException e) {
            return GSON.toJson(getJsonResult(
                    1,
                    "error",
                    "Не удалось преобразовать дату. Формат даты не соответствует yyyy-MM-dd",
                    null,
                    "/getGridOfWeek"));
        }

        try {
            RpcGetGridOfWeek.GridAndParams gap = NetCommander.getGridOfWeek(NET_PROPERTY, serviceId, startDate, -1);

            final GregorianCalendar gc = new GregorianCalendar();
            gc.setTime(startDate);
            gc.set(GregorianCalendar.DAY_OF_WEEK, 2);
            gc.set(GregorianCalendar.HOUR_OF_DAY, 0);
            gc.set(GregorianCalendar.MINUTE, 0);

            gap.getTimes().forEach((dd) -> {
                final GregorianCalendar gcClient = new GregorianCalendar();
                final GregorianCalendar gcNow = new GregorianCalendar();
                gcClient.setTime(dd);
                gcNow.setTime(new Date());
                // проверим не ушел ли пользователь слишком далеко, куда уже нельзя
                final boolean f = listSoFare(gap, gcClient, gcNow);
                if (f) {
                    list.add(HUMAN_FORMAT.format(dd.getTime()));
                }
            });

            return GSON.toJson(getJsonResult(
                    0,
                    "ok",
                    "Список свободных дат для " + mfcName + " на " + date,
                    GSON.toJsonTree(list),
                    "/getGridOfWeek"));

        } catch (Exception e) {
            return GSON.toJson(getJsonResult(
                    1,
                    "error",
                    "Не удалось получить список свободных дат. Возможно услуги с id = " + serviceId + " не существует",
                    null,
                    "/getGridOfWeek"));
        }
    }

    @POST
    @Path("/standInService")
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public String standInService(Customer customer, Long serviceId, String date) {
        return "";
    }

    @Override
    public Boolean chekBeforeStandInService(Long serviceId, Date date) throws Exception {
        final CmdParams cmdParams = new CmdParams();
        cmdParams.serviceId = serviceId;
        cmdParams.date = date.getTime();

        final Gson gson = GsonPool.getInstance().borrowGson();
        final RpcGetBool rpc;

        String res = NetCommander.send(NET_PROPERTY, "CHEK_BEFORE_PRE-ENTRY", cmdParams);
        rpc = gson.fromJson(res, RpcGetBool.class);
        return rpc.getResult();
    }

    @Override
    public List<PreEntry> getListPreEntryCustomer(Customer customer) {
        return null;
    }

    @Override
    public Boolean cancelPreEntry(PreEntry preEntry) {
        return null;
    }

    //**************************************************************************

    private JsonObject getJsonResult(Integer code, String status, String message, JsonElement result, String path) {

        final JsonObject jsonObject = new JsonObject();
        final DateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss:SSS", Locale.ENGLISH);

        jsonObject.addProperty("timestamp", format.format(new Date()));
        jsonObject.addProperty("code", code);
        jsonObject.addProperty("status", status);
        jsonObject.addProperty("message", message);
        jsonObject.add("result", result);
        jsonObject.addProperty("path", path);

        return jsonObject;
    }

    private boolean listSoFare(RpcGetGridOfWeek.GridAndParams res, GregorianCalendar gcClient, GregorianCalendar gcNow) {
        // проверим не ушел ли пользователь слишком далеко, куда уже нельзя
        boolean f = true;
        int per = 0;
        if (gcClient.get(GregorianCalendar.DAY_OF_YEAR) - gcNow.get(GregorianCalendar.DAY_OF_YEAR) > 0) {
            per = gcClient.get(GregorianCalendar.DAY_OF_YEAR) - gcNow.get(GregorianCalendar.DAY_OF_YEAR);
        } else {
            per = gcClient.get(GregorianCalendar.DAY_OF_YEAR) + (gcNow.isLeapYear(gcNow.get(GregorianCalendar.YEAR)) ? 365 : 366 - gcNow.get(GregorianCalendar.DAY_OF_YEAR));
        }
        if (per > res.getAdvanceLimitPeriod() && res.getAdvanceLimitPeriod() != 0) {
            f = false;
        }
        return f;
    }
}
