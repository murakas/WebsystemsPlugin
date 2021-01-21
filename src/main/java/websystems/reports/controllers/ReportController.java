package websystems.reports.controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.log4j.Log4j2;
import websystems.reports.models.*;
import ru.apertum.qsystem.hibernate.Dao;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Log4j2
@Path("/api/report")
public class ReportController {

    private final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Отчет по пользователям.
     *
     * @param dateFrom дата с
     * @param dateTo   дата по
     * @return ReportUser
     */
    @GET
    @Path("/reportUser")
    @Produces(MediaType.APPLICATION_JSON)
    public String reportUser(@QueryParam("dateFrom") String dateFrom, @QueryParam("dateTo") String dateTo) {

        String sql =
                "SELECT " +
                        "u.NAME AS 'userName'," +
                        "CONVERT_TZ( s.user_start_time, '+00:00', '+03:00' ) AS 'startTime'," +
                        "CONCAT( c.service_prefix, c.number ) AS 'ticket'" +
                        "FROM " +
                        "qsystem.statistic s " +
                        "LEFT JOIN qsystem.users u ON u.id = s.user_id " +
                        "LEFT JOIN qsystem.clients c ON c.id = s.client_id " +
                        "WHERE " +
                        "CAST( s.user_start_time AS DATE ) >= ? and CAST( s.user_start_time AS DATE ) <= ? " +
                        "ORDER BY " +
                        "u.NAME, 'Время начала работы'";
        List<ResponseReportUser> responseReportUsers = new ArrayList<>();
        Dao.get().execute(() -> Dao.get().getSession().doWork((Connection connection) -> {
            final PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, dateFrom);
            preparedStatement.setString(2, dateTo);
            try (ResultSet set = preparedStatement.executeQuery()) {
                while (set.next()) {
                    responseReportUsers.add(new ResponseReportUser(set.getString("userName"), set.getString("startTime"), set.getString("ticket")));
                }
            }
        }));
        return GSON.toJson(responseReportUsers);
    }

    /**
     * Отчет по среднему времени ожидания.
     *
     * @param dateFrom дата с
     * @param dateTo   дата по
     * @return ReportAverageWaitingTime
     */
    @GET
    @Path("/reportAverageWaitingTime")
    @Produces(MediaType.APPLICATION_JSON)
    public String reportAverageWaitingTime(@QueryParam("dateFrom") String dateFrom, @QueryParam("dateTo") String dateTo) {

        String sql =
                "SELECT " +
                        "u.`name` AS 'userName'," +
                        "round( AVG( s.client_wait_period ) ) AS 'averageWaitingTime' " +
                        "FROM " +
                        "statistic s " +
                        "LEFT JOIN users u ON u.id = s.user_id " +
                        "WHERE " +
                        "state_in <> 0 " +
                        "AND DATE( client_stand_time ) >= ? " +
                        "AND DATE( client_stand_time ) <= ? " +
                        "GROUP BY u.`name` " +
                        "ORDER BY u.`name`";
        List<ResponseAvgWaitingTime> responseAvgWaitingTimes = new ArrayList<>();
        Dao.get().execute(() -> Dao.get().getSession().doWork((Connection connection) -> {
            final PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, dateFrom);
            preparedStatement.setString(2, dateTo);
            try (ResultSet set = preparedStatement.executeQuery()) {
                while (set.next()) {
                    responseAvgWaitingTimes.add(new ResponseAvgWaitingTime(set.getString("userName"), set.getString("averageWaitingTime")));
                }
            }
        }));
        return GSON.toJson(responseAvgWaitingTimes);
    }

    /**
     * Время простоя оператора между услугами.
     *
     * @param dateFrom дата с
     * @param dateTo   дата по
     * @return ResponseOperatorIdle
     */
    @GET
    @Path("/getOperatorIdle")
    @Produces(MediaType.APPLICATION_JSON)
    public String getOperatorIdle(@QueryParam("dateFrom") String dateFrom, @QueryParam("dateTo") String dateTo) throws ParseException {

        String sql =
                "select date(s.client_stand_time) as 'date'," +
                        "s.user_uuid, " +
                        "DATE_ADD(s.user_start_time, INTERVAL 3 HOUR) as 'user_start_time', " +
                        "DATE_ADD(s.user_finish_time, INTERVAL 3 HOUR) as 'user_finish_time' " +
                        "from " +
                        "qsystem.statistic s " +
                        "where " +
                        "date(s.client_stand_time) >= ? " +
                        "and date(s.client_stand_time) <= ? " +
                        "order by " +
                        "s.user_id, s.user_start_time, s.user_finish_time";
        List<OperatorIdle> operatorIdles = new ArrayList<>();
        Dao.get().execute(() -> Dao.get().getSession().doWork((Connection connection) -> {
            final PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, dateFrom);
            preparedStatement.setString(2, dateTo);
            try (ResultSet set = preparedStatement.executeQuery()) {
                while (set.next()) {
                    operatorIdles.add(new OperatorIdle(
                            set.getString("date"),
                            set.getString("user_start_time"),
                            set.getString("user_finish_time"),
                            set.getString("user_uuid")));
                }
            }
        }));

        operatorIdles.sort(Comparator.comparing(OperatorIdle::getUserUuid).thenComparing(OperatorIdle::getUserStartTime));

        List<ResponseOperatorIdle> responseOperatorIdles = new ArrayList<>();
        if (operatorIdles.size() > 0) {

            Set<String> userUuid = new HashSet<>();
            Set<String> date = new HashSet<>();

            operatorIdles.stream()
                    .filter(n -> !userUuid.add(n.getUserUuid()))
                    .collect(Collectors.toSet());

            operatorIdles.stream()
                    .filter(n -> !date.add(n.getDate()))
                    .collect(Collectors.toSet());

            userUuid.stream().forEach(s -> System.out.println(s));

            for (String uu : userUuid) {
                for (String dd : date) {
                    List<OperatorIdle> tmpList = operatorIdles.stream()
                            .filter(operatorIdle -> uu.equals(operatorIdle.getUserUuid()) && dd.equals(operatorIdle.getDate()))
                            .collect(Collectors.toList());
                    responseOperatorIdles = Stream.concat(responseOperatorIdles.stream(),
                            calculateOperatorIdle(tmpList).stream()).collect(Collectors.toList());
                }
            }
        }

        return GSON.toJson(responseOperatorIdles);
    }

    private List<ResponseOperatorIdle> calculateOperatorIdle(List<OperatorIdle> sortedOperatorIdles) throws ParseException {
        List<ResponseOperatorIdle> responseOperatorIdles = new ArrayList<>();
        if (sortedOperatorIdles.size() > 0) {
            for (int i = 1; i < sortedOperatorIdles.size(); i++) {
                OperatorIdle current = sortedOperatorIdles.get(i);
                OperatorIdle previous = sortedOperatorIdles.get(i - 1);

                ResponseOperatorIdle responseOperatorIdle = new ResponseOperatorIdle();
                responseOperatorIdle.setDate(current.getDate());
                responseOperatorIdle.setNextServiceStart(current.getUserStartTime());
                responseOperatorIdle.setPreviousServiceEnd(previous.getUserFinishTime());
                responseOperatorIdle.setUserUuid(current.getUserUuid());

                DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                Date dateN = format.parse(current.getUserStartTime());
                Date dateP = format.parse(previous.getUserFinishTime());
                long milliseconds = dateN.getTime() - dateP.getTime();
                long diffMinutes = milliseconds / (60L * 1000);
                responseOperatorIdle.setIdleTime(String.valueOf(diffMinutes));

                responseOperatorIdles.add(responseOperatorIdle);
            }
        }
        return responseOperatorIdles;
    }

    /**
     * Время ожидания.
     *
     * @param dateFrom дата с
     * @param dateTo   дата по
     * @return ResponseWaitingTime
     */
    @GET
    @Path("/getWaitingTime")
    @Produces(MediaType.APPLICATION_JSON)
    public String getWaitingTime(@QueryParam("dateFrom") String dateFrom, @QueryParam("dateTo") String dateTo) {
        String sql = "select " +
                "date(DATE_ADD(s.user_start_time, interval 3 hour)) as 'date', " +
                "DATE_ADD(s.client_stand_time, interval 3 hour) as 'stand_time', " +
                "DATE_ADD(s.user_start_time, interval 3 hour) as 'start_time', " +
                "concat(c.service_prefix, c.`number`) as 'ticket', " +
                "s2.name as 'service', " +
                "s.client_wait_period as 'wait_time' " +
                "from qsystem.statistic s " +
                "left join qsystem.clients c on c.id = s.client_id " +
                "left join qsystem.services s2 on s2.id = c.service_id " +
                "where " +
                "date(s.user_start_time) >= ? " +
                "and date(s.user_start_time) <= ? ";
        List<ResponseWaitingTime> waitingTimes = new ArrayList<>();


        Dao.get().execute(() -> Dao.get().getSession().doWork((Connection connection) -> {
            final PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, dateFrom);
            preparedStatement.setString(2, dateTo);
            try (ResultSet set = preparedStatement.executeQuery()) {
                while (set.next()) {
                    waitingTimes.add(new ResponseWaitingTime(
                            set.getString("date"),
                            set.getString("stand_time"),
                            set.getString("start_time"),
                            set.getString("ticket"),
                            set.getString("service"),
                            set.getString("wait_time")));
                }
            }
        }));
        return GSON.toJson(waitingTimes);
    }

    /**
     * Отчет по филиалам.
     *
     * @param dateFrom дата с
     * @param dateTo   дата по
     * @return ResponseBranchReport
     */
    @GET
    @Path("/getBranchReport")
    @Produces(MediaType.APPLICATION_JSON)
    public String getBranchReport(@QueryParam("dateFrom") String dateFrom, @QueryParam("dateTo") String dateTo) {
        String sql =
                "select " +
                        "count(s.id) as quantity_clients," +
                        "ROUND(avg(s.client_wait_period), 1) as avg_client_wait_period," +
                        "ROUND(avg(TIME_TO_SEC(timediff(s.user_finish_time, s.user_start_time))/ 60)) as avg_time_work " +
                        "from " +
                        "qsystem.statistic s " +
                        "where " +
                        "date (s.client_stand_time) >= ? " +
                        "and date (s.client_stand_time) <= ?";
        List<ResponseBranchReport> responseBranchReports = new ArrayList<>();

        Dao.get().execute(() -> Dao.get().getSession().doWork((Connection connection) -> {
            final PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, dateFrom);
            preparedStatement.setString(2, dateTo);
            try (ResultSet set = preparedStatement.executeQuery()) {
                while (set.next()) {
                    responseBranchReports.add(new ResponseBranchReport(
                            set.getString("quantity_clients"),
                            set.getString("avg_client_wait_period"),
                            set.getString("avg_time_work"))
                    );
                }
            }
        }));
        return GSON.toJson(responseBranchReports);
    }

}
