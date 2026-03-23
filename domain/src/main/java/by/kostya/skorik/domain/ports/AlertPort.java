package by.kostya.skorik.domain.ports;

import by.kostya.skorik.domain.model.Alerts;

import java.time.LocalDateTime;
import java.util.List;

public interface AlertPort {
    void save(Alerts alerts);
    List<Alerts> getAlertsBetweenTime(LocalDateTime start, LocalDateTime end);
    List<Alerts> getAlertsBetweenTimeAndRouterId(LocalDateTime start, LocalDateTime end, Long routerId);
}
