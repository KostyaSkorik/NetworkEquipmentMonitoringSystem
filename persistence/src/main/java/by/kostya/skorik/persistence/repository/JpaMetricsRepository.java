package by.kostya.skorik.persistence.repository;

import by.kostya.skorik.persistence.entity.MetricsEntity;
import by.kostya.skorik.persistence.entity.RouterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface JpaMetricsRepository extends JpaRepository<MetricsEntity, Long> {

    List<MetricsEntity> getAllByPollingTimeBetweenOrderByPollingTimeAsc(LocalDateTime pollingTimeAfter, LocalDateTime pollingTimeBefore);

    void deleteByPollingTimeBefore(LocalDateTime pollingTimeBefore);

    @Query(value = "SELECT DISTINCT ON (router_id, interface_name) * " +
                   "FROM metrics " +
                   "ORDER BY router_id, interface_name, polling_time DESC",
            nativeQuery = true)
    List<MetricsEntity> findAllLastMetrics();

    List<MetricsEntity> getAllByPollingTimeBetweenAndRouterOrderByPollingTimeAsc(LocalDateTime pollingTimeAfter,
                                                                                 LocalDateTime pollingTimeBefore,
                                                                                 RouterEntity router);
}
