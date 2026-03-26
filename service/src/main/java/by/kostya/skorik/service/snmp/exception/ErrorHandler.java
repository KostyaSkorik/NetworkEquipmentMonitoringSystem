package by.kostya.skorik.service.snmp.exception;

import by.kostya.skorik.domain.model.Router;

public interface ErrorHandler {
    void alertHandler(Throwable ex, Router router);
}
