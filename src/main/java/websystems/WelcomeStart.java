package websystems;

import lombok.extern.log4j.Log4j2;
import ru.apertum.qsystem.client.model.QButton;
import ru.apertum.qsystem.common.cmd.RpcGetAllServices;
import ru.apertum.qsystem.common.model.IClientNetProperty;
import ru.apertum.qsystem.common.model.INetProperty;
import ru.apertum.qsystem.common.model.QCustomer;
import ru.apertum.qsystem.extra.IWelcome;
import ru.apertum.qsystem.server.model.QAdvanceCustomer;
import ru.apertum.qsystem.server.model.QService;

import java.awt.*;

@Log4j2()
public class WelcomeStart implements IWelcome {
    public static void main(String[] args) {

    }

    @Override
    public void start(IClientNetProperty iClientNetProperty, RpcGetAllServices.ServicesForWelcome servicesForWelcome) {
    }

    @Override
    public boolean showPreInfoDialog(QButton qButton, Frame frame, INetProperty iNetProperty, String s, String s1, boolean b, boolean b1, int i) {
        return false;
    }

    @Override
    public String showInputDialog(QButton qButton, Frame frame, boolean b, INetProperty iNetProperty, boolean b1, int i, String s, QService qService) {
        return null;
    }

    @Override
    public StandInParameters handleStandInParams(QButton qButton, StandInParameters standInParameters) {
        return standInParameters;
    }

    @Override
    public boolean buttonPressed(QButton qButton, QService qService) {
        return true;
    }

    @Override
    public void readyNewCustomer(QButton qButton, QCustomer qCustomer, QService qService) {
    }

    @Override
    public void readyNewAdvCustomer(QButton qButton, QAdvanceCustomer qAdvanceCustomer, QService qService) {

    }

    @Override
    public String getDescription() {
        return "WebSystemsPlugin";
    }

    @Override
    public long getUID() {
        return 322;
    }
}
