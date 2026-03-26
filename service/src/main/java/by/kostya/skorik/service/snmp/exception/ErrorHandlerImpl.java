package by.kostya.skorik.service.snmp.exception;

import by.kostya.skorik.domain.model.Alerts;
import by.kostya.skorik.domain.model.Router;
import by.kostya.skorik.domain.ports.AlertPort;
import by.kostya.skorik.service.snmp.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ErrorHandlerImpl implements ErrorHandler {

    private final NotificationService notificationService;
    private final AlertPort alertPort;

    @Override
    public void alertHandler(Throwable ex, Router router){

        String message = "Роутер не доступен, возможно проблема с портом, который опрашивается " + ex.getMessage();
        Alerts alerts = new Alerts();
        alerts.setRouterName(router.getName());
        alerts.setIpSource(router.getIp());
        alerts.setTime(LocalDateTime.now());
        alerts.setTrapType("Interface does not work");
        alerts.setMessage(message);
        alertPort.save(alerts);
        notificationService.sendAlerts(alerts);
    }
}
