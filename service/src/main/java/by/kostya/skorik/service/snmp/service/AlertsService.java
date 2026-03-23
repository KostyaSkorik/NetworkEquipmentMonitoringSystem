package by.kostya.skorik.service.snmp.service;

import by.kostya.skorik.domain.dto.AlertsDto;

import java.time.LocalDateTime;
import java.util.List;

public interface AlertsService {
    List<AlertsDto> getByTimeBetween(LocalDateTime start, LocalDateTime end);

    List<AlertsDto> getByTimeBetweenAndRouterId(LocalDateTime start, LocalDateTime end, Long routerId);
}