package by.kostya.skorik.service.snmp.service;

import by.kostya.skorik.domain.model.Metrics;

public interface CalculateMetrics {
    void calculateBandwidth(Metrics metrics, Metrics lastMetrics);

    void calculateUtilization(Metrics metrics, Double maxSpeed);
}