package by.kostya.skorik.persistence.repository;

import by.kostya.skorik.persistence.entity.AlertEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface JpaAlertsRepository extends JpaRepository<AlertEntity, Long> {
    List<AlertEntity> getAllByTimeBetween(LocalDateTime timeAfter, LocalDateTime timeBefore);

    List<AlertEntity> getAllByTimeBetweenAndRouterName(LocalDateTime timeAfter, LocalDateTime timeBefore, String routerName);
}