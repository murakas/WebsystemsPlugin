package websystems.webclient.controllers;

import ru.apertum.qsystem.client.model.QButton;
import ru.apertum.qsystem.common.cmd.RpcGetAllServices;
import ru.apertum.qsystem.common.model.IClientNetProperty;
import ru.apertum.qsystem.common.model.INetProperty;
import ru.apertum.qsystem.common.model.QCustomer;
import ru.apertum.qsystem.extra.IWelcome;
import ru.apertum.qsystem.server.model.QAdvanceCustomer;
import ru.apertum.qsystem.server.model.QService;

import java.awt.*;

public class WelcomeController implements IWelcome {

    @Override
    public void start(IClientNetProperty iClientNetProperty, RpcGetAllServices.ServicesForWelcome servicesForWelcome) {
        //AppSettings.init(iClientNetProperty);
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
        return false;
    }

    @Override
    public void readyNewCustomer(QButton qButton, QCustomer qCustomer, QService qService) {
        //MdmController.standInService(qCustomer);
    }

    @Override
    public void readyNewAdvCustomer(QButton qButton, QAdvanceCustomer qAdvanceCustomer, QService qService) {

    }

    @Override
    public String getDescription() {
        return "Плагин МДМ для терминала.";
    }

    @Override
    public long getUID() {
        return 654L;
    }
}
