package websystems.controllers.preentry;

import websystems.domains.preentry.Customer;
import websystems.domains.preentry.PreEntry;

import java.util.Date;
import java.util.List;

public interface IPreEntry {
    /**
     * Возвращает список доступных услуг для предварительной записи
     */
    String getServiceList();

    /**
     * Возвращает список доступного времени на неделю
     */
    String getGridOfWeek(Long servicesId, String date, String mfcName);

    /**
     * Предварительная запись
     */
    String standInService(Customer customer, Long serviceId, String date);

    /**
     * Перед тем как записать проверим, может пока думали кто-то уже записался
     */
    Boolean chekBeforeStandInService(Long serviceId, Date date) throws Exception;

    /**
     * Получить список ранее предварительных записей для заявителя
     */
    List<PreEntry> getListPreEntryCustomer(Customer customer);

    /**
     * Отмена предварительной записи
     */
    Boolean cancelPreEntry(PreEntry preEntry);
}
