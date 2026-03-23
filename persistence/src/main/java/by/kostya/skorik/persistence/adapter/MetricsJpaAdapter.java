package by.kostya.skorik.persistence.adapter;

import by.kostya.skorik.domain.model.Metrics;
import by.kostya.skorik.domain.ports.MetricsPort;
import by.kostya.skorik.persistence.entity.MetricsEntity;
import by.kostya.skorik.persistence.entity.RouterEntity;
import by.kostya.skorik.persistence.mapper.MetricsMapper;
import by.kostya.skorik.persistence.repository.JpaMetricsRepository;
import by.kostya.skorik.persistence.repository.JpaRouterRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class MetricsJpaAdapter implements MetricsPort {

    private final JpaMetricsRepository jpaMetricsRepository;
    private final JpaRouterRepository jpaRouterRepository;
    private final MetricsMapper metricsMapper;

    @Override
    public void save(Metrics metrics) {
        RouterEntity routerEntity = jpaRouterRepository
                .findById(metrics.getRouterId())
                .orElseThrow(() -> new EntityNotFoundException("Router not found with this id"));

        MetricsEntity metricsEntity = metricsMapper.modelToEntity(metrics);
        metricsEntity.setRouter(routerEntity);

        jpaMetricsRepository.save(metricsEntity);
    }

    @Override
    public List<Metrics> getMetricsByTime(LocalDateTime start, LocalDateTime end) {
        return jpaMetricsRepository.getAllByPollingTimeBetweenOrderByPollingTimeAsc(start, end)
                .stream()
                .map(metricsMapper::entityToModel).toList();
    }

    @Override
    public List<Metrics> getMetricsByTimeAndRouterId(LocalDateTime start, LocalDateTime end, Long routerId) {
        RouterEntity routerEntity = jpaRouterRepository
                .findById(routerId)
                .orElseThrow(() -> new EntityNotFoundException("Router not found with this id"));
        return jpaMetricsRepository
                .getAllByPollingTimeBetweenAndRouterOrderByPollingTimeAsc(start,end, routerEntity)
                .stream()
                .map(metricsMapper::entityToModel)
                .toList();
    }

    @Override
    public Optional<Metrics> getLastSavedMetrics(Long routerId, String interfaceName) {
        RouterEntity routerEntity = jpaRouterRepository
                .findById(routerId)
                .orElseThrow(() -> new EntityNotFoundException("Router not found with this id"));
        return jpaMetricsRepository
                .findFirstByRouterAndInterfaceNameOrderByPollingTimeDesc(routerEntity, interfaceName)
                .map(metricsMapper::entityToModel);
    }

    @Override
    public List<Metrics> getLastMetrics() {
        return jpaMetricsRepository
                .findAllLastMetrics()
                .stream()
                .map(metricsMapper::entityToModel)
                .toList();
    }


}
