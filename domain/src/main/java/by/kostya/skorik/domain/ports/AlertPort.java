package by.kostya.skorik.domain.ports;

import by.kostya.skorik.domain.dto.AlertDto;

import java.time.LocalDateTime;
import java.util.List;

public interface AlertPort {
    void save(AlertDto alertDto);
    List<AlertDto> getAlertsBetweenTime(LocalDateTime start, LocalDateTime end);
    List<AlertDto> getAlertsBetweenTimeAndRouterId(LocalDateTime start, LocalDateTime end, Long routerId);
}
