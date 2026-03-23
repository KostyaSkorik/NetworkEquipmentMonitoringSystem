package by.kostya.skorik.persistence.adapter;

import by.kostya.skorik.domain.model.Router;
import by.kostya.skorik.domain.ports.RouterPort;
import by.kostya.skorik.persistence.entity.RouterEntity;
import by.kostya.skorik.persistence.mapper.RouterMapper;
import by.kostya.skorik.persistence.repository.JpaRouterRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class RouterJpaAdapter implements RouterPort {

    private final JpaRouterRepository jpaRouterRepository;
    private final RouterMapper routerMapper;

    @Override
    public boolean save(Router router) {
        RouterEntity savedRouter = jpaRouterRepository.save(routerMapper.modelToEntity(router));
        return savedRouter.getId() != null;
    }

    @Override
    public Router getRouterById(Long id) {
        return jpaRouterRepository.findById(id)
                .map(routerMapper::entityToModel)
                .orElseThrow(() -> new EntityNotFoundException("Router not found with this id"));
    }

    @Override
    public List<Router> findAllRouters() {
        return jpaRouterRepository.findAll().stream().map(routerMapper::entityToModel).toList();
    }

    @Override
    public Router findByIp(String ip) {
        return jpaRouterRepository.findByIp(ip)
                .map(routerMapper::entityToModel)
                .orElseThrow(() -> new EntityNotFoundException("Router nor found with this ip"));
    }

    @Override
    public void updateRouter(Long id, Router router) {
        RouterEntity routerEntity = jpaRouterRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Router nor found with this ip"));
        RouterEntity newRouter = routerMapper.modelToEntity(router);
        routerMapper.updateRouter(newRouter,routerEntity);
        jpaRouterRepository.save(routerEntity);
    }

    @Override
    public void deleteRouter(Long id) {
        RouterEntity routerEntity = jpaRouterRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Router nor found with this ip"));
        jpaRouterRepository.delete(routerEntity);
    }
}
