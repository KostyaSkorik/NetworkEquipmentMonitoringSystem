package by.kostya.skorik.service.snmp.service.impl;

import by.kostya.skorik.domain.dto.MetricsDto;
import by.kostya.skorik.domain.ports.MetricsPort;
import by.kostya.skorik.service.snmp.mapper.MetricsDtoMapper;
import by.kostya.skorik.service.snmp.service.MetricsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MetricsServiceImpl implements MetricsService {
    private final MetricsPort metricsPort;
    private final MetricsDtoMapper metricsDtoMapper;

    @Override
    public List<MetricsDto> getByTimeBetween(LocalDateTime start, LocalDateTime end) {
        return metricsPort
                .getMetricsByTime(start,end)
                .stream()
                .map(metricsDtoMapper::modelToDto)
                .toList();
    }

    @Override
    public List<MetricsDto> getByTimeBetweenAndRouterId(LocalDateTime start, LocalDateTime end, Long routerId) {
        return metricsPort
                .getMetricsByTimeAndRouterId(start,end,routerId)
                .stream()
                .map(metricsDtoMapper::modelToDto)
                .toList();
    }
}
