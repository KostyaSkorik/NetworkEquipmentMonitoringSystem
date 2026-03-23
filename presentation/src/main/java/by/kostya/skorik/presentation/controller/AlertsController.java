package by.kostya.skorik.presentation.controller;

import by.kostya.skorik.domain.dto.AlertsDto;
import by.kostya.skorik.service.snmp.service.AlertsService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController("api/alerts")
@RequiredArgsConstructor
public class AlertsController {
    private final AlertsService alertsService;

    @GetMapping("/getAlertsByTime")
    public List<AlertsDto> getByTime(@RequestParam("start")
                                     @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                     @RequestParam("end")
                                     @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end) {

        return alertsService.getByTimeBetween(start, end);
    }

    @GetMapping("/getAlertsByTimeAndRouterId/{id}")
    public List<AlertsDto> getByTimeAndRouter(@RequestParam("start")
                                              @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                              @RequestParam("end")
                                              @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                              @PathVariable("id") Long id) {

        return alertsService.getByTimeBetweenAndRouterId(start, end, id);
    }
}
