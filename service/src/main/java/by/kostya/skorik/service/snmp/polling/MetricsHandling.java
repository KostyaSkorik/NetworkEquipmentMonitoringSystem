package by.kostya.skorik.service.snmp.polling;

import by.kostya.skorik.domain.model.InterfaceStatus;
import by.kostya.skorik.domain.model.Metrics;
import by.kostya.skorik.domain.model.Router;
import by.kostya.skorik.domain.ports.MetricsPort;
import by.kostya.skorik.domain.ports.RouterPort;
import by.kostya.skorik.service.snmp.exception.ErrorHandler;
import by.kostya.skorik.service.snmp.service.CalculateMetrics;
import by.kostya.skorik.service.snmp.trap.AlertListeningService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.util.TableEvent;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class MetricsHandling {
    private final SNMPPolling snmpPolling;
    private final MetricsPort metricsPort;
    private final RouterPort routerPort;
    private final ErrorHandler errorHandler;
    private final CalculateMetrics calculateMetrics;
    private final AlertListeningService alertListeningService;


    /*не забыть добавить важное уточнение, что просто так отправить 10.1.2.2 не получится
    необходимо добавить sudo ip route add 10.0.0.0/8 via 192.168.56.10
    */
    @Scheduled(fixedRate = 30000)
    public void pollingInterface() {
        Map<String, Metrics> lastMetricsMap = new ConcurrentHashMap<>();
        fillMap(lastMetricsMap);
        List<Router> routers = routerPort.findAllRouters();
        for (Router router : routers) {
            log.info("ЗАПРОС НА РОУТЕР {} ПОШЕЛ TIME:{}", router.getName(), System.currentTimeMillis());
            CompletableFuture<List<TableEvent>> tableEventsFuture = snmpPolling.getMetricsTable("udp:%s/161".formatted(router.getIp()));
            tableEventsFuture
                    .exceptionally(ex -> {
                        errorHandler.alertHandler(ex, router);
                        return null;
                    })
                    .thenApply(result -> fillMetrics(result, router.getId(), lastMetricsMap))
                    .thenApply(metrics -> {
                        metricsPort.saveAll(metrics);
                        return metrics;
                    })
                    .thenAccept(metrics -> alertListeningService.checkUtilization(metrics,router))
                    .thenRun(() -> log.info("ЗАПРОС НА РОУТЕР {} ОКОНЧЕН TIME:{}", router.getName(), System.currentTimeMillis()));
        }
    }

    public void fillMap(Map<String, Metrics> lastMetricsMap) {
        List<Metrics> lestMetrics = metricsPort.getLastMetrics();
        for (Metrics metrics : lestMetrics) {
            lastMetricsMap.putIfAbsent(metrics.getRouterId() + "." + metrics.getInterfaceName(), metrics);
        }
    }

    public List<Metrics> fillMetrics(List<TableEvent> events, Long routerId, Map<String, Metrics> lastMetricsMap) {
        if (events == null) {
            return Collections.emptyList();
        }
        List<Metrics> metricsList = new ArrayList<>();
        for (TableEvent event : events) {
            Metrics metrics = new Metrics();
            VariableBinding[] columns = event.getColumns();

            Metrics lastMetrics = lastMetricsMap.get(routerId + "." + columns[0].toValueString());
            if (columns[0].toValueString().equals("Null0")) {
                continue;
            }
            metrics.setPollingTime(LocalDateTime.now());
            metrics.setRouterId(routerId);
            metrics.setInterfaceName(columns[0].toValueString());
            metrics.setInputCounter(Long.valueOf(columns[1].toValueString()));
            metrics.setOutputCounter(Long.valueOf(columns[2].toValueString()));

            if (lastMetrics != null) {
                calculateMetrics.calculateBandwidth(metrics, lastMetrics);
                calculateMetrics.calculateUtilization(metrics, Double.valueOf(columns[4].toValueString()));
            } else {
                metrics.setInputBandwidth(0.0);
                metrics.setOutputBandwidth(0.0);
                metrics.setInputUtilization(0.0);
                metrics.setOutputUtilization(0.0);
            }

            metrics.setStatus(InterfaceStatus.valueOf(columns[3].toValueString().equals("1") ? "UP" : "DOWN"));

            metricsList.add(metrics);
        }
        return metricsList;
    }
}