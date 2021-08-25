package websystems.utils;

import lombok.extern.log4j.Log4j2;
import ru.apertum.qsystem.client.QProperties;
import ru.apertum.qsystem.common.exceptions.QException;
import ru.apertum.qsystem.common.model.INetProperty;

import java.util.Properties;
import java.util.UUID;

/**
 * Хранитель конфига :).
 *
 * @author Murad
 */

@Log4j2
public class AppSettings {

    private static final String KEY = "murad89064814600";
    private static final String INIT_VECTOR = "RandomInitVector";

    private AppSettings() {
        PROPERTIES = new Properties();
    }

    private final Properties PROPERTIES;
    private static final AppSettings SINGLETON;
    private static Boolean status = false;

    static {
        SINGLETON = new AppSettings();
    }

    public static Object get(String key) {
        return SINGLETON.PROPERTIES.get(key);
    }


    public static Object get(String key, Object deflt) {
        Object obj = SINGLETON.PROPERTIES.get(key);
        if (obj == null) {
            return deflt;
        } else {
            return obj;
        }
    }

    public static int getInt(String key, int deflt) {
        Object obj = SINGLETON.PROPERTIES.get(key);
        if (obj == null) {
            return deflt;
        } else {
            return Integer.parseInt((String) obj);
        }
    }

    public static String getString() {
        return SINGLETON.PROPERTIES.toString();
    }

    public static void put(String key, Object data) {
        if (data == null) {
            throw new IllegalArgumentException();
        } else {
            SINGLETON.PROPERTIES.put(key, data);
        }
    }

    public static void init(INetProperty property) throws QException {
        if (!status) {
            String section = "mdmApi";
            log.debug("Получаем настройки для МДМ");
            System.out.println(property.getAddress() + ":" + property.getPort());
            QProperties.get().load(property, true);
            QProperties.get().getAllProperties().stream().forEach(System.out::println);
            try {
//                AppSettings.put("apiUri", QProperties.get().getProperty(section, "apiUri").getValue());
//                AppSettings.put("tokenUri", QProperties.get().getProperty(section, "tokenUri").getValue());
//                AppSettings.put("username", QProperties.get().getProperty(section, "username").getValue());
//                AppSettings.put("password", EncryptorPass.decrypt(KEY, INIT_VECTOR, QProperties.get().getProperty(section, "password").getValue()));
                AppSettings.put("mfcUUID", UUID.fromString(QProperties.get().getProperty(section, "mfcUUID").getValue()));
                status = true;
            } catch (NullPointerException e) {
                log.error("Ошибка, в базе данных отсутствуют некоторые поля, проверьте базу данных", e);
            }
            //HttpClientApi.getToken();
        }
    }
}
