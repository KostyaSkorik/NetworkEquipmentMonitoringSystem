package by.kostya.skorik.presentation.controller;

import by.kostya.skorik.domain.dto.AlertsDto;
import by.kostya.skorik.service.snmp.service.AlertsService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RequestMapping("api/alerts")
@RequiredArgsConstructor
@RestController
//TODO описать методы и их работу в интерфейсе
public class AlertsController {
    private final AlertsService alertsService;

    @GetMapping
    public List<AlertsDto> getByTime(@RequestParam("start")
                                     @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                     @RequestParam("end")
                                     @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end) {

        return alertsService.getByTimeBetween(start, end);
    }

    @GetMapping("/{id}")
    public List<AlertsDto> getByTimeAndRouter(@RequestParam("start")
                                              @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                              @RequestParam("end")
                                              @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                              @PathVariable("id") Long routerId) {

        return alertsService.getByTimeBetweenAndRouterId(start, end, routerId);
    }
}
