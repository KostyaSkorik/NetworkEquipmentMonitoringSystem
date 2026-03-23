package by.kostya.skorik.service.snmp.polling;

import by.kostya.skorik.domain.model.InterfaceStatus;
import by.kostya.skorik.domain.model.Metrics;
import by.kostya.skorik.domain.model.Router;
import by.kostya.skorik.domain.ports.MetricsPort;
import by.kostya.skorik.domain.ports.RouterPort;
import lombok.RequiredArgsConstructor;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.util.TableEvent;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MetricsHandling {
    private final SNMPPolling snmpPolling;
    private final MetricsPort metricsPort;
    private final RouterPort routerPort;

    /*не забыть добавить важное уточнение, что просто так отправить 10.1.2.2 не получится
    необходимо добавить sudo ip route add 10.0.0.0/8 via 192.168.56.10
    */
    @Scheduled(fixedRate = 30000)
    public void pollingInterface() {
        List<Router> routers = routerPort.findAllRouters();
        //TODO опрашивать устройства асинхронно
        for (Router router : routers) {
            List<TableEvent> tableEvents = snmpPolling.getMetricsTable("udp:%s/161".formatted(router.getIp()));
            List<Metrics> metrics = fillMetrics(tableEvents, router.getId());
            //TODO сохранять разом с помощью saveAll()
            for (Metrics metric : metrics) {
                metricsPort.save(metric);
            }
        }
    }

    private List<Metrics> fillMetrics(List<TableEvent> events, Long routerId) {
        List<Metrics> metricsList = new ArrayList<>();

        for (TableEvent event : events) {
            Metrics metrics = new Metrics();
            VariableBinding[] columns = event.getColumns();

            Optional<Metrics> lastMetricsOpt = metricsPort.getLastSavedMetrics(routerId, columns[0].toValueString());
            if (columns[0].toValueString().equals("Null0")) {
                continue;
            }
            metrics.setPollingTime(LocalDateTime.now());
            metrics.setRouterId(routerId);
            metrics.setInterfaceName(columns[0].toValueString());
            metrics.setInputCounter(Long.valueOf(columns[1].toValueString()));
            metrics.setOutputCounter(Long.valueOf(columns[2].toValueString()));

            if (lastMetricsOpt.isPresent()) {
                Metrics lastMetrics = lastMetricsOpt.get();
                metrics.setInputBandwidth(inputBandwidth(metrics, lastMetrics));
                metrics.setOutputBandwidth(outputBandwidth(metrics, lastMetrics));

                metrics.setInputUtilization(inputUtilization(metrics.getInputBandwidth(), Double.valueOf(columns[4].toValueString())));
                metrics.setOutputUtilization(outputUtilization(metrics.getOutputBandwidth(), Double.valueOf(columns[4].toValueString())));

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

    private Double inputUtilization(Double currentInputSpeed, Double maxSpeed) {
        return (currentInputSpeed / maxSpeed) * 100.0;
    }

    private Double outputUtilization(Double currentOutputSpeed, Double maxSpeed) {
        return (currentOutputSpeed / maxSpeed) * 100.0;
    }

    private Double inputBandwidth(Metrics currentMetrics, Metrics lastMetrics) {
        long current = currentMetrics.getInputCounter();
        long last = lastMetrics.getInputCounter();
        return commonBandwidth(currentMetrics, lastMetrics, current, last);
    }

    private Double outputBandwidth(Metrics currentMetrics, Metrics lastMetrics) {
        long current = currentMetrics.getOutputCounter();
        long last = lastMetrics.getOutputCounter();
        return commonBandwidth(currentMetrics, lastMetrics, current, last);
    }

    private Double commonBandwidth(Metrics currentMetrics, Metrics lastMetrics, long current, long last) {
        long difCounter;
        if (current >= last) {
            difCounter = current - last;
        } else {
            difCounter = (4294967296L - last) + current;
        }

        double difTime = Duration.between(lastMetrics.getPollingTime(), currentMetrics.getPollingTime())
                                 .toMillis() / 1000.0;
        return (difCounter * 8) / difTime;
    }
}