package websystems.utils;

import ru.apertum.qsystem.common.AUdpServer;

import java.net.InetAddress;

public class UdpServer extends AUdpServer {

    public UdpServer(int port) {
        super(port);
        WSSHandler.start();
    }

    @Override
    protected void getData(String s, InetAddress inetAddress, int clientPort) {
        WSSHandler.newCustomer(s);
    }
}
