package by.kostya.skorik.persistence.repository;

import by.kostya.skorik.persistence.entity.MetricsEntity;
import by.kostya.skorik.persistence.entity.RouterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface JpaMetricsRepository extends JpaRepository<MetricsEntity,Long> {

    List<MetricsEntity> getAllByPollingTimeBetweenOrderByPollingTimeAsc(LocalDateTime pollingTimeAfter, LocalDateTime pollingTimeBefore);

    Optional<MetricsEntity> findFirstByRouterAndInterfaceNameOrderByPollingTimeDesc(RouterEntity router,
                                                                                    String interfaceName);

    void deleteByPollingTimeBefore(LocalDateTime pollingTimeBefore);

    @Query("SELECT m FROM MetricsEntity m WHERE date_trunc('second',m.pollingTime) = " +
           "(SELECT date_trunc('second', max(m2.pollingTime)) FROM MetricsEntity m2) ")
    List<MetricsEntity> findAllLastMetrics();

    List<MetricsEntity> getAllByPollingTimeBetweenAndRouterOrderByPollingTimeAsc(LocalDateTime pollingTimeAfter,
                                                            LocalDateTime pollingTimeBefore,
                                                            RouterEntity router);
}
