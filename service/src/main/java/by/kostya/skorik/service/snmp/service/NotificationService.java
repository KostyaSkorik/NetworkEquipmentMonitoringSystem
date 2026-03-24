package by.kostya.skorik.service.snmp.service;

import by.kostya.skorik.domain.model.Alerts;

public interface NotificationService {
    void sendAlerts(Alerts alerts);
}
