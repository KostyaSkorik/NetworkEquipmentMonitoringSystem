package by.kostya.skorik.service.snmp.service.impl;

import by.kostya.skorik.domain.model.Metrics;
import by.kostya.skorik.service.snmp.service.CalculateMetrics;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class CalculateMetricsImpl implements CalculateMetrics {
    @Override
    public void calculateBandwidth(Metrics metrics, Metrics lastMetrics) {
        metrics.setInputBandwidth(inputBandwidth(metrics, lastMetrics));
        metrics.setOutputBandwidth(outputBandwidth(metrics, lastMetrics));
    }

    @Override
    public void calculateUtilization(Metrics metrics, Double maxSpeed) {
        metrics.setInputUtilization(inputUtilization(metrics.getInputBandwidth(), maxSpeed));
        metrics.setOutputUtilization(outputUtilization(metrics.getOutputBandwidth(), maxSpeed));
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

    private Double inputUtilization(Double currentInputSpeed, Double maxSpeed) {
        return (currentInputSpeed / maxSpeed) * 100.0;
    }

    private Double outputUtilization(Double currentOutputSpeed, Double maxSpeed) {
        return (currentOutputSpeed / maxSpeed) * 100.0;
    }
}