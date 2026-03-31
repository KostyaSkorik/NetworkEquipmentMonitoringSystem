package by.kostya.skorik.presentation.controller;

import by.kostya.skorik.domain.dto.MetricsDto;
import by.kostya.skorik.service.snmp.service.MetricsService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RequestMapping("api/metrics")
@RequiredArgsConstructor
@RestController
public class MetricsController {
    private final MetricsService metricsService;

    @GetMapping
    public List<MetricsDto> getAllByTime(@RequestParam("start")
                                         @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                         @RequestParam("end")
                                         @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end) {
        return metricsService.getByTimeBetween(start, end);
    }

    @GetMapping("/{id}")
    public List<MetricsDto> getAllByTime(@RequestParam("start")
                                         @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                         @RequestParam("end")
                                         @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                         @PathVariable("id") Long routerId) {
        return metricsService.getByTimeBetweenAndRouterId(start, end, routerId);
    }
}