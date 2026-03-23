package by.kostya.skorik.persistence.adapter;

import by.kostya.skorik.domain.dto.AlertDto;
import by.kostya.skorik.domain.ports.AlertPort;
import by.kostya.skorik.persistence.entity.RouterEntity;
import by.kostya.skorik.persistence.mapper.AlertMapper;
import by.kostya.skorik.persistence.repository.JpaAlertsRepository;
import by.kostya.skorik.persistence.repository.JpaRouterRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AlertsJpaAdapter implements AlertPort{
    private final JpaAlertsRepository jpaAlertsRepository;
    private final JpaRouterRepository jpaRouterRepository;
    private final AlertMapper alertMapper;

    @Override
    public void save(AlertDto alertDto) {
        jpaAlertsRepository.save(alertMapper.dtoToEntity(alertDto));
    }

    @Override
    public List<AlertDto> getAlertsBetweenTime(LocalDateTime start, LocalDateTime end) {
        return jpaAlertsRepository
                .getAllByTimeBetween(start,end)
                .stream()
                .map(alertMapper::entityToDto)
                .toList();
    }

    @Override
    public List<AlertDto> getAlertsBetweenTimeAndRouterId(LocalDateTime start, LocalDateTime end, Long routerId) {
        RouterEntity router = jpaRouterRepository
                .findById(routerId)
                .orElseThrow(()->new EntityNotFoundException("Router not found with this id"));
        return jpaAlertsRepository
                .getAllByTimeBetweenAndRouterName(start,end,router.getName())
                .stream()
                .map(alertMapper::entityToDto)
                .toList();
    }
}
