package by.kostya.skorik.domain.model;

import lombok.*;

import java.time.LocalDateTime;

@Data
public class Metrics {
    private LocalDateTime pollingTime;
    private Long routerId;
    private String interfaceName;
    private Long inputCounter;
    private Long outputCounter;
    private Double inputBandwidth;
    private Double outputBandwidth;
    private Double inputUtilization;
    private Double outputUtilization;
    private InterfaceStatus status;
}