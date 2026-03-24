package by.kostya.skorik.presentation.controller;

import by.kostya.skorik.domain.dto.MetricsDto;
import by.kostya.skorik.service.snmp.service.MetricsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController("api/metrics")
@RequiredArgsConstructor
//TODO описать методы и их работу в интерфейсе
public class MetricsController {
    private final MetricsService metricsService;

    @GetMapping("/getMetricsByTime/{start}/{end}")
    public List<MetricsDto> getAllByTime(@PathVariable("start") LocalDateTime start, @PathVariable("end") LocalDateTime end) {
        return metricsService.getByTimeBetween(start, end);
    }

    @PostMapping("/getMetricsByTimeAndRouter/{start}/{end}/{id}")
    public List<MetricsDto> getAllByTime(@PathVariable("start") LocalDateTime start,
                                         @PathVariable("end") LocalDateTime end,
                                         @PathVariable("id") Long id) {
        return metricsService.getByTimeBetweenAndRouterId(start, end, id);
    }
}