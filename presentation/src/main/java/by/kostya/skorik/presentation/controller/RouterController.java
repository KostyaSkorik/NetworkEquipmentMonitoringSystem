package by.kostya.skorik.presentation.controller;

import by.kostya.skorik.domain.dto.RouterDto;
import by.kostya.skorik.service.snmp.service.NotificationService;
import by.kostya.skorik.service.snmp.service.RouterService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("api/router")
@RequiredArgsConstructor
//TODO описать методы и их работу в интерфейсе
public class RouterController {
    private final RouterService routerService;

    @GetMapping("/findALl")
    public List<RouterDto> findAllRouter(){
        return routerService.findAllRouters();
    }

    @PostMapping("/save")
    public void saveRouter (RouterDto routerDto){
        routerService.saveRouter(routerDto);
    }

    @PutMapping("/update/{id}")
    public void updateRouter(@PathVariable("id") Long routerId, RouterDto newRouter){
        routerService.updateRouter(routerId,newRouter);
    }

    @DeleteMapping("/delete")
    public void deleteRouter(Long routerId){
        routerService.deleteRouter(routerId);
    }

}
