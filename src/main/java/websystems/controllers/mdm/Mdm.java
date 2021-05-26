//package websystems.controllers.mdm;
//
//import com.google.gson.Gson;
//import lombok.extern.log4j.Log4j2;
//import ru.apertum.qsystem.server.model.QUser;
//import websystems.models.mdm.DataMdmObjectsAttributesUpload;
//import websystems.models.mdm.DataMdmObjectsUpload;
//import websystems.models.mdm.ObjectsIdentifierMfc;
//import websystems.utils.HttpClientApi;
//import ru.apertum.qsystem.common.model.QCustomer;
//
//import java.io.BufferedWriter;
//import java.io.FileWriter;
//import java.text.SimpleDateFormat;
//import java.util.*;
//
///**
// * Выполняет команды по сохранению состояний кастомера для МДМ.
// */
//@Log4j2
//public class Mdm {
//    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.s Z");
//    private static final Gson GSON = new Gson();
//
//    private static void send(ArrayList<DataMdmObjectsAttributesUpload> attributesArrayList, Integer objectTypeId) {
//        try {
//            final DataMdmObjectsUpload mdmObject = new DataMdmObjectsUpload();
//            //mdmObject.setSprEmployeesMfcId(UUID.fromString("123e4567-e89b-12d3-a456-426655440000"));//UUID.fromString(AppSettings.get("mfcUUID").toString()));
//            //mdmObject.setDataServicesId(UUID.fromString("00000000-0000-0000-0000-000000000000"));
//            //mdmObject.setAttributes(attributesArrayList);
//            String s = GSON.toJson(mdmObject, DataMdmObjectsUpload.class);
//            System.out.println(s);
//            if (objectTypeId == 2 || objectTypeId == 4) {
//                BufferedWriter writer = new BufferedWriter(new FileWriter("d:/welcome.txt", true));
//                writer.append('\n' + objectTypeId + "\n");
//                writer.append(s);
//                writer.close();
//            }
//            //HttpClientApi.saveMdmObjects(gson.toJson(mdmObject, DataMdmObjectsUpload.class));
//        } catch (Exception e) {
//            log.error("МДМ. Ошибка при сохранения данных", e);
//        }
//    }
//
//    /**
//     * Пришел новый кастомер. Талон выдан (сформирован).
//     *
//     * @param customer заявитель
//     */
//    public static void standInService(QCustomer customer) {
//        try {
//            if (customer != null) {
//                log.debug("МДМ плагин. Выдадача талона " + customer.getFullNumber());
//
//                ObjectsIdentifierMfc objectsIdentifierMfc = new ObjectsIdentifierMfc();
//                objectsIdentifierMfc.setTicketFormation(UUID.randomUUID().toString());
//                objectsIdentifierMfc.setTicketIssued(UUID.randomUUID().toString());
//                //Пока будем хранить здесь
//                customer.setInputData(GSON.toJson(objectsIdentifierMfc));
//                /**
//                 * ******** Ticked Issued 4 ********
//                 */
//                ArrayList<DataMdmObjectsAttributesUpload> attributesUploads4 = new ArrayList<>();
//                attributesUploads4.add(new DataMdmObjectsAttributesUpload(10, objectsIdentifierMfc.getTicketFormation()));
//                attributesUploads4.add(new DataMdmObjectsAttributesUpload(224, "0"));
//                attributesUploads4.add(new DataMdmObjectsAttributesUpload(202, customer.getStandTime() + " +03"));
//                attributesUploads4.add(new DataMdmObjectsAttributesUpload(152, "0"));
//                send(attributesUploads4, 4);
//
//                /**
//                 * ******** Ticked Issued 2 ********
//                 */
//                //сдвинем на 5 сек выдачу талона относительно времени формирования талона
//                Calendar calendar = Calendar.getInstance();
//                calendar.setTime(customer.getStandTime());
//                calendar.add(Calendar.SECOND, 5);
//
//                final ArrayList<DataMdmObjectsAttributesUpload> attributesUploads2 = new ArrayList<>();
//                attributesUploads2.add(new DataMdmObjectsAttributesUpload(2, objectsIdentifierMfc.getTicketIssued()));
//                attributesUploads2.add(new DataMdmObjectsAttributesUpload(3, calendar.getTime() + " +03"));
//                attributesUploads2.add(new DataMdmObjectsAttributesUpload(4, customer.getId().toString()));
//                attributesUploads2.add(new DataMdmObjectsAttributesUpload(223, ""));
//
//                send(attributesUploads2, 2);
//
//            } else {
//                log.error("МДМ плагин [standInService]. Ошибка, кастомер почему-то null, проверь код :(");
//            }
//        } catch (Exception e) {
//            log.error("МДМ плагин [standInService]." + e.getMessage());
//        }
//    }
//
//    //
//    public static void inviteNextCustomer(QCustomer customer) {
//        if (customer != null) {
//            log.debug("Вызов талона " + customer.getFullNumber());
//
//            ObjectsIdentifierMfc objectsIdentifierMfc = GSON.fromJson(customer.getInputData(), ObjectsIdentifierMfc.class);
//            objectsIdentifierMfc.setTicketCalled(UUID.randomUUID().toString());
//
//            /**
//             * ******** Ticked Issued 3 ********
//             */
//            final ArrayList<DataMdmObjectsAttributesUpload> attributesUploads = new ArrayList<>();
//            attributesUploads.add(new DataMdmObjectsAttributesUpload(7, objectsIdentifierMfc.getTicketCalled()));
//            attributesUploads.add(new DataMdmObjectsAttributesUpload(8, DATE_FORMAT.format(customer.getCallTime() + " +03")));
//            attributesUploads.add(new DataMdmObjectsAttributesUpload(187, customer.getUser().getId().toString()));
//            attributesUploads.add(new DataMdmObjectsAttributesUpload(192, objectsIdentifierMfc.getTicketIssued()));
//
//            customer.setInputData(GSON.toJson(objectsIdentifierMfc));
//
//            send(attributesUploads, 3);
//        } else {
//            log.error("Ошибка вызов талона " + customer.getFullNumber() + " кастомер почему-то null, проверь код :(");
//        }
//    }
//
//    //
////    public static void invitePostponeCustomer(QCustomer customer) {
////        if (customer != null) {
////            log.debug("МДМ плагин. Вызовим отложенного кастомера под номером " + customer.getFullNumber());
////            /**
////             * ******** Ticked Issued 2 ********
////             */
////            final ArrayList<DataMdmObjectsAttributesUpload> attributesArrayList7 = new ArrayList<>();
////            attributesArrayList7.add(new DataMdmObjectsAttributesUpload(28, customer.getId().toString()));
////            attributesArrayList7.add(new DataMdmObjectsAttributesUpload(29, DATE_FORMAT.format(new Date(customer.getFinishPontpone()))));
////            attributesArrayList7.add(new DataMdmObjectsAttributesUpload(30, customer.getUser().getPoint()));
////            attributesArrayList7.add(new DataMdmObjectsAttributesUpload(31, customer.getId().toString()));
////            attributesArrayList7.add(new DataMdmObjectsAttributesUpload(32, customer.getUser().getUserUuid()));
////
////            send(attributesArrayList7, 7);
////        } else {
////            log.error("МДМ плагин (invitePostponeCustomer). Ошибка, кастомер почему-то null, проверь код :(");
////        }
////    }
////
////    public static void standInServiceAdvance(QAdvanceCustomer qac) {
////    }
////
////    public static void getStartCustomer(QCustomer customer) {
////        if (customer != null) {
////            log.debug("МДМ плагин (getStartCustomer). Начнем работу с кастомером под номером " + customer.getFullNumber());
////
////            final ArrayList<DataMdmObjectsAttributesUpload> attributesArrayList5 = new ArrayList<>();
////            attributesArrayList5.add(new DataMdmObjectsAttributesUpload(15, customer.getStartTime() + " +03"));
////            attributesArrayList5.add(new DataMdmObjectsAttributesUpload(16, customer.getUser().getPoint()));
////            attributesArrayList5.add(new DataMdmObjectsAttributesUpload(17, customer.getStandTime() + " +03"));
////            attributesArrayList5.add(new DataMdmObjectsAttributesUpload(18, customer.getUser().getPoint()));
////            attributesArrayList5.add(new DataMdmObjectsAttributesUpload(19, customer.getId().toString()));
////            attributesArrayList5.add(new DataMdmObjectsAttributesUpload(20, customer.getId().toString()));
////
////            send(attributesArrayList5, 5);
////        } else {
////            log.error("МДМ плагин (getStartCustomer). Ошибка, клиент = null.");
////        }
////
////    }
////
////    public static void customerToPostpone(QCustomer customer) {
////        if (customer != null) {
////
////            log.debug("МДМ плагин. Выдадим талон новому кастомеру = " + customer.getFullNumber());
////
////            final ArrayList<DataMdmObjectsAttributesUpload> attributesArrayList6 = new ArrayList<>();
////            attributesArrayList6.add(new DataMdmObjectsAttributesUpload(21, customer.getId().toString()));
////            attributesArrayList6.add(new DataMdmObjectsAttributesUpload(22, DATE_FORMAT.format(new Date(customer.getStartPontpone()))));
////            attributesArrayList6.add(new DataMdmObjectsAttributesUpload(23, customer.getId().toString()));
////            attributesArrayList6.add(new DataMdmObjectsAttributesUpload(24, customer.getUser().getUserUuid()));
////            attributesArrayList6.add(new DataMdmObjectsAttributesUpload(25, customer.getUser().getPoint()));
////            attributesArrayList6.add(new DataMdmObjectsAttributesUpload(26, ""));
////            attributesArrayList6.add(new DataMdmObjectsAttributesUpload(27, ""));
////
////            send(attributesArrayList6, 6);
////
////        } else {
////            log.error("МДМ плагин (customerToPostpone). Ошибка, кастомер почему-то null, проверь код :(");
////        }
////    }
////
//
//    /**
//     * Завершаем работу с клиентом.
//     *
//     * @param customer клиент
//     * @param user     пользователь
//     */
//    public static void getFinishCustomer(QCustomer customer, QUser user, String userUuid) {
//        if (customer != null) {
//            ObjectsIdentifierMfc objectsIdentifierMfc = GSON.fromJson(customer.getInputData(), ObjectsIdentifierMfc.class);
//            objectsIdentifierMfc.setTicketResult(UUID.randomUUID().toString());
//
//            final ArrayList<DataMdmObjectsAttributesUpload> attributesUploads = new ArrayList<>();
//
//            attributesUploads.add(new DataMdmObjectsAttributesUpload(153, objectsIdentifierMfc.getTicketResult()));
//            attributesUploads.add(new DataMdmObjectsAttributesUpload(193, objectsIdentifierMfc.getTicketCalled()));
//            attributesUploads.add(new DataMdmObjectsAttributesUpload(155, "0"));
//            attributesUploads.add(new DataMdmObjectsAttributesUpload(216, DATE_FORMAT.format(customer.getFinishTime() + " +03")));
//
//            customer.setInputData(GSON.toJson(objectsIdentifierMfc));
//
//            send(attributesUploads, 34);
//        } else {
//            log.error("MDMLog-[getFinishCustomer]. Ошибка, кастомер почему-то null, проверь код :(");
//        }
//    }
//
////    /**
////     * 4 - формирование талона.
////     * 2 - выдача талона.
////     * 3 - вызов талона.
////     * 34 - завершение обработки талона.
////     *
////     * @param customer - клиент
////     * @param userId   - пользователь
////     */
////    public static void save_4_2_3_34_mdmObjects(QCustomer customer, long userId) {
////        final ArrayList<DataMdmObjectsAttributesUpload> attributesArrayList4 = new ArrayList<>();
////        attributesArrayList4.add(new DataMdmObjectsAttributesUpload(10, customer.getId().toString()));
////        attributesArrayList4.add(new DataMdmObjectsAttributesUpload(224, "0"));
////        Date date = customer.getStandTime();
////        attributesArrayList4.add(new DataMdmObjectsAttributesUpload(202, DATE_FORMAT.format(date)));
////        attributesArrayList4.add(new DataMdmObjectsAttributesUpload(152, "0"));
////
////        final ArrayList<DataMdmObjectsAttributesUpload> attributesArrayList2 = new ArrayList<>();
////        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/Moscow"));
////        calendar.setTime(customer.getStandTime());
////        calendar.add(Calendar.SECOND, 1);
////        System.out.println(calendar.getTime().toString());
////        attributesArrayList2.add(new DataMdmObjectsAttributesUpload(2, customer.getId().toString()));
////        attributesArrayList2.add(new DataMdmObjectsAttributesUpload(3, DATE_FORMAT.format(calendar.getTime())));
////        attributesArrayList2.add(new DataMdmObjectsAttributesUpload(4, customer.getId().toString()));
////
////        final ArrayList<DataMdmObjectsAttributesUpload> attributesArrayList3 = new ArrayList<>();
////        attributesArrayList3.add(new DataMdmObjectsAttributesUpload(7, customer.getId().toString()));
////        attributesArrayList3.add(new DataMdmObjectsAttributesUpload(8, DATE_FORMAT.format(customer.getStartTime())));
////        attributesArrayList3.add(new DataMdmObjectsAttributesUpload(187, String.valueOf(userId)));
////        attributesArrayList3.add(new DataMdmObjectsAttributesUpload(192, customer.getId().toString()));
////
////        final ArrayList<DataMdmObjectsAttributesUpload> attributesArrayList34 = new ArrayList<>();
////        attributesArrayList34.add(new DataMdmObjectsAttributesUpload(153, customer.getId().toString()));
////        attributesArrayList34.add(new DataMdmObjectsAttributesUpload(193, customer.getId().toString()));
////        attributesArrayList34.add(new DataMdmObjectsAttributesUpload(155, "0"));
////        attributesArrayList34.add(new DataMdmObjectsAttributesUpload(216, DATE_FORMAT.format(customer.getFinishTime())));
////
////        send(attributesArrayList4, 4);
////        send(attributesArrayList2, 2);
////        send(attributesArrayList3, 3);
////        send(attributesArrayList34, 34);
////        DATE_FORMAT.format(new Date(customer.getFinishPontpone()));
////    }
//}
