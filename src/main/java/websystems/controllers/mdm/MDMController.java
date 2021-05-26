package websystems.controllers.mdm;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.log4j.Log4j2;
import ru.apertum.qsystem.hibernate.Dao;
import websystems.models.mdm.DataMdmObjectsAttributesUpload;
import websystems.models.mdm.DataMdmObjectsUpload;
import websystems.utils.AppSettings;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.UUID;

@Log4j2
@Path("/api/mdm")
public class MDMController {
    private final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final DateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss:SSS", Locale.ENGLISH);
    private final String mfcUuid = AppSettings.get("mfcUUID").toString();

    /**
     * Отчет по пользователям.
     *
     * @param dateFrom дата с
     * @param dateTo   дата по
     * @return ReportUser
     */
    @GET
    @Path("/getData")
    @Produces(MediaType.APPLICATION_JSON)
    public String getData(@QueryParam("dateFrom") String dateFrom, @QueryParam("dateTo") String dateTo) {

        String sql = "select " +
                "'0' as '4_152', " +
                "DATE_ADD(s.client_stand_time, interval 3 hour) as '4_202', " +
                "'0' as '4_224', " +
                "DATE_ADD(s.client_stand_time, interval FLOOR(RAND()*(10805-10802 + 1))+ 10802 second) as '2_3', " +
                "DATE_ADD(s.user_start_time, interval 3 hour) as '3_8', " +
                "s.user_id as '3_187', " +
                "'0' as '34_155', " +
                "DATE_ADD(s.user_finish_time, interval 3 hour) as '34_216' " +
                "from " +
                "qsystem.statistic s " +
                "where " +
                "date(s.client_stand_time) >= ? " +
                "and date(s.client_stand_time) <= ? " +
                "order by " +
                "s.user_id, " +
                "s.client_stand_time";

        ArrayList<DataMdmObjectsUpload> mdmObjectsUploads = new ArrayList<>();

        Dao.get().execute(() -> Dao.get().getSession().doWork((Connection connection) -> {
            final PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, dateFrom);
            preparedStatement.setString(2, dateTo);
            try (ResultSet set = preparedStatement.executeQuery()) {
                while (set.next()) {

                    String uuid4 = UUID.randomUUID().toString();
                    String uuid2 = UUID.randomUUID().toString();
                    String uuid3 = UUID.randomUUID().toString();
                    String uuid34 = UUID.randomUUID().toString();
                    /**************************************************************************************************/

                    DataMdmObjectsUpload upload4 = new DataMdmObjectsUpload();
                    ArrayList<DataMdmObjectsAttributesUpload> attributes4 = new ArrayList<>();

                    attributes4.add(new DataMdmObjectsAttributesUpload(10, uuid4));
                    attributes4.add(new DataMdmObjectsAttributesUpload(224, set.getString(3)));
                    attributes4.add(new DataMdmObjectsAttributesUpload(191, ""));
                    attributes4.add(new DataMdmObjectsAttributesUpload(152, set.getString(1)));
                    attributes4.add(new DataMdmObjectsAttributesUpload(202, set.getString(2) + "+03"));

                    upload4.setAttribute(attributes4);
                    upload4.setCreatedDate(set.getString(2));
                    upload4.setSprMdmObjectTypeId(4);
                    upload4.setSprEmployeesMfcId(mfcUuid);
                    mdmObjectsUploads.add(upload4);
                    /**************************************************************************************************/

                    DataMdmObjectsUpload upload2 = new DataMdmObjectsUpload();
                    ArrayList<DataMdmObjectsAttributesUpload> attributes2 = new ArrayList<>();

                    attributes2.add(new DataMdmObjectsAttributesUpload(2, uuid2));
                    attributes2.add(new DataMdmObjectsAttributesUpload(3, set.getString(4) + "+03"));
                    attributes2.add(new DataMdmObjectsAttributesUpload(4, uuid4));
                    attributes2.add(new DataMdmObjectsAttributesUpload(223, ""));

                    upload2.setAttribute(attributes2);
                    upload2.setCreatedDate(set.getString(3));
                    upload2.setSprMdmObjectTypeId(2);
                    upload2.setSprEmployeesMfcId(mfcUuid);
                    mdmObjectsUploads.add(upload2);
                    /**************************************************************************************************/

                    DataMdmObjectsUpload upload3 = new DataMdmObjectsUpload();
                    ArrayList<DataMdmObjectsAttributesUpload> attributes3 = new ArrayList<>();

                    attributes3.add(new DataMdmObjectsAttributesUpload(7, uuid3));
                    attributes3.add(new DataMdmObjectsAttributesUpload(8, set.getString(5) + "+03"));
                    attributes3.add(new DataMdmObjectsAttributesUpload(192, uuid2));
                    attributes3.add(new DataMdmObjectsAttributesUpload(187, set.getString(6)));

                    upload3.setAttribute(attributes3);
                    upload3.setCreatedDate(set.getString(5));
                    upload3.setSprMdmObjectTypeId(3);
                    upload3.setSprEmployeesMfcId(mfcUuid);
                    mdmObjectsUploads.add(upload3);
                    /**************************************************************************************************/
                    DataMdmObjectsUpload upload34 = new DataMdmObjectsUpload();
                    ArrayList<DataMdmObjectsAttributesUpload> attributes34 = new ArrayList<>();

                    attributes34.add(new DataMdmObjectsAttributesUpload(153, uuid34));
                    attributes34.add(new DataMdmObjectsAttributesUpload(193, uuid3));
                    attributes34.add(new DataMdmObjectsAttributesUpload(155, set.getString(7)));
                    attributes34.add(new DataMdmObjectsAttributesUpload(216, set.getString(8) + "+03"));

                    upload34.setAttribute(attributes34);
                    upload34.setCreatedDate(set.getString(8));
                    upload34.setSprMdmObjectTypeId(34);
                    upload34.setSprEmployeesMfcId(mfcUuid);
                    mdmObjectsUploads.add(upload34);
                }
            }
        }));
        return GSON.toJson(mdmObjectsUploads);
    }
}
