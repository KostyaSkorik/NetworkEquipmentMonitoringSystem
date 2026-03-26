package by.kostya.skorik.presentation.controller;

import by.kostya.skorik.domain.dto.RouterDto;
import by.kostya.skorik.service.snmp.service.RouterService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("api/router")
@RequiredArgsConstructor
@RestController
//TODO описать методы и их работу в интерфейсе
public class RouterController {
    private final RouterService routerService;

    @GetMapping
    public List<RouterDto> findAllRouter() {
        return routerService.findAllRouters();
    }

    @PostMapping
    public void saveRouter(@RequestBody RouterDto routerDto) {
        routerService.saveRouter(routerDto);
    }

    @PutMapping("/{id}")
    public void updateRouter(@PathVariable("id") Long routerId, RouterDto newRouter) {
        routerService.updateRouter(routerId, newRouter);
    }

    @DeleteMapping("/{id}")
    public void deleteRouter(@PathVariable("id") Long routerId) {
        routerService.deleteRouter(routerId);
    }

}
