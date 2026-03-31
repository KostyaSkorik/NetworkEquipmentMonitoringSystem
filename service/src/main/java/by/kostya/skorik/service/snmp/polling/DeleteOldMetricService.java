package by.kostya.skorik.service.snmp.polling;

import by.kostya.skorik.persistence.repository.JpaMetricsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DeleteOldMetricService {
    private final JpaMetricsRepository jpaMetricsRepository;

    @Scheduled(cron = "0 */5 * * * *")
    @Transactional
    public void delete() {
        LocalDateTime deleteTime = LocalDateTime.now().minusMinutes(5);
        jpaMetricsRepository.deleteByPollingTimeBefore(deleteTime);
    }
}