package by.kostya.skorik.persistence.repository;

import by.kostya.skorik.persistence.entity.MetricsEntity;
import by.kostya.skorik.persistence.entity.RouterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface JpaMetricsRepository extends JpaRepository<MetricsEntity,Long> {

    List<MetricsEntity> getAllByPollingTimeBetween(LocalDateTime pollingTimeAfter, LocalDateTime pollingTimeBefore);

    Optional<MetricsEntity> findFirstByRouterAndInterfaceNameOrderByPollingTimeDesc(RouterEntity router,
                                                                                    String interfaceName);

}
