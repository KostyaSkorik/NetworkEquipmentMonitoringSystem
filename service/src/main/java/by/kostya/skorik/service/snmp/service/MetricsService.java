package by.kostya.skorik.service.snmp.service;

import by.kostya.skorik.domain.dto.MetricsDto;

import java.time.LocalDateTime;
import java.util.List;

public interface MetricsService {
    List<MetricsDto> getByTimeBetween(LocalDateTime start, LocalDateTime end);

    List<MetricsDto> getByTimeBetweenAndRouterId(LocalDateTime start, LocalDateTime end, Long routerId);
}