package by.kostya.skorik.domain.ports;

import by.kostya.skorik.domain.model.Metrics;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MetricsPort {

    void save(Metrics metrics);
    List<Metrics> getMetricsByTime(LocalDateTime start, LocalDateTime end);
    List<Metrics> getMetricsByTimeAndRouterId(LocalDateTime start, LocalDateTime end, Long routerId);
    Optional<Metrics> getLastSavedMetrics(Long routerId, String interfaceName);
    List<Metrics> getLastMetrics();
}
