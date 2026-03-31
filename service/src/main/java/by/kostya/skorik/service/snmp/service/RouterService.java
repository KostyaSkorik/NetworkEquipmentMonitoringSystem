package by.kostya.skorik.service.snmp.service;

import by.kostya.skorik.domain.dto.RouterDto;

import java.util.List;

public interface RouterService {
    void saveRouter(RouterDto routerDto);

    List<RouterDto> findAllRouters();

    void updateRouter(Long routerId, RouterDto newRouter);

    void deleteRouter(Long routerId);
}