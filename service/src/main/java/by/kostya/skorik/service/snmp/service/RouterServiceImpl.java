package by.kostya.skorik.service.snmp.service;

import by.kostya.skorik.domain.dto.RouterDto;
import by.kostya.skorik.domain.ports.RouterPort;
import by.kostya.skorik.service.snmp.mapper.RouterDtoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RouterServiceImpl implements RouterService {
    private final RouterPort routerPort;
    private final RouterDtoMapper routerDtoMapper;

    @Override
    public void saveRouter(RouterDto routerDto) {
        routerPort.save(routerDtoMapper.dtoToModel(routerDto));
    }

    @Override
    public List<RouterDto> findAllRouters() {
        return routerPort
                .findAllRouters()
                .stream()
                .map(routerDtoMapper::modelToDto)
                .toList();
    }

    @Override
    public void updateRouter(Long routerId, RouterDto newRouter) {
        routerPort.updateRouter(routerId, routerDtoMapper.dtoToModel(newRouter));
    }

    @Override
    public void deleteRouter(Long routerId) {
        routerPort.deleteRouter(routerId);
    }
}