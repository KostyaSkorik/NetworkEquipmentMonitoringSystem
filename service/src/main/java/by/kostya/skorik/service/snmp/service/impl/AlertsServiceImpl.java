package by.kostya.skorik.service.snmp.service.impl;

import by.kostya.skorik.domain.dto.AlertsDto;
import by.kostya.skorik.domain.ports.AlertPort;
import by.kostya.skorik.service.snmp.mapper.AlertsDtoMapper;
import by.kostya.skorik.service.snmp.service.AlertsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AlertsServiceImpl implements AlertsService {
    private final AlertPort alertPort;
    private final AlertsDtoMapper alertsDtoMapper;

    @Override
    public List<AlertsDto> getByTimeBetween(LocalDateTime start, LocalDateTime end) {
        return alertPort
                .getAlertsBetweenTime(start, end)
                .stream()
                .map(alertsDtoMapper::modelToDto)
                .toList();
    }

    @Override
    public List<AlertsDto> getByTimeBetweenAndRouterId(LocalDateTime start, LocalDateTime end, Long routerId) {
        return alertPort
                .getAlertsBetweenTimeAndRouterId(start, end, routerId)
                .stream()
                .map(alertsDtoMapper::modelToDto)
                .toList();
    }
}