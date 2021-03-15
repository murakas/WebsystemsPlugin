package websystems;

import lombok.extern.log4j.Log4j2;
import websystems.utils.AppSettings;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;
import ru.apertum.qsystem.common.exceptions.QException;
import ru.apertum.qsystem.common.model.INetProperty;
import ru.apertum.qsystem.extra.IStartServer;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

import static org.eclipse.jetty.servlet.ServletContextHandler.NO_SESSIONS;

@Log4j2()
public class ServerStart implements IStartServer {

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
                throw new RuntimeException("Invalid server address: " + ex);
            }
        }
    };

    public static void main(String[] args) {

        try {
            InetAddress ip = InetAddress.getLocalHost();
            System.out.println("Current IP address : " + ip.getHostAddress());

            NetworkInterface network = NetworkInterface.getByInetAddress(ip);

            byte[] mac = network.getHardwareAddress();

            System.out.print("Current MAC address : ");

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < mac.length; i++) {
                sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
            }
            System.out.println(sb.toString());

        } catch (Exception e) {
            log.error("Ошибка запуска QWebClient сервера", e);
            System.exit(1);
        }

        try {
            AppSettings.init(NET_PROPERTY);
        } catch (QException e) {
            log.error("Ошибка инициализации AppSettings", e);
        }

        Server server = new Server(8008);

        ServletContextHandler servletContextHandler = new ServletContextHandler(NO_SESSIONS);

        servletContextHandler.setContextPath("/");
        server.setHandler(servletContextHandler);
        ServletHolder servletHolder = servletContextHandler.addServlet(ServletContainer.class, "/queue/*");
        servletHolder.setInitOrder(0);
        servletHolder.setInitParameter(
                "jersey.config.server.provider.packages",
                "websystems"
        );


        try {
            server.start();
            server.join();
        } catch (Exception e) {
            log.error("Ошибка запуска QWebClient сервера", e);
            System.exit(1);
        } finally {
            server.destroy();
        }
    }

    /**
     * Старту сервера.
     */
    @Override
    public void start() {
        try {
            TimeUnit.SECONDS.sleep(20);
        } catch (Exception e) {
            System.err.println("Ошибка ожидания 20 сек.");
        }

        try {
            AppSettings.init(NET_PROPERTY);
        } catch (QException e) {
            log.error("Ошибка инициализации AppSettings", e);
        }

        Server server = new Server(8008);

        ServletContextHandler servletContextHandler = new ServletContextHandler(NO_SESSIONS);

        servletContextHandler.setContextPath("/");
        server.setHandler(servletContextHandler);

        ServletHolder servletHolder = servletContextHandler.addServlet(ServletContainer.class, "/queue/*");
        servletHolder.setInitOrder(0);
        servletHolder.setInitParameter("jersey.config.server.provider.packages", "websystems");
        try {
            server.start();
            server.join();
            log.info("Сервер QWebClient запущен на 8008 порту");
        } catch (Exception e) {
            log.error("Ошибка. QWebClient не запустился", e);
            System.exit(1);
        } finally {
            server.destroy();
        }
    }

    @Override
    public String getDescription() {
        return "QWebClient";
    }

    @Override
    public long getUID() {
        return 321;
    }

}