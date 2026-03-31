package by.kostya.skorik.domain.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MetricsDto {
    private LocalDateTime pollingTime;
    private Long routerId;
    private String interfaceName;
    private Long inputCounter;
    private Long outputCounter;
    private Double inputBandwidth;
    private Double outputBandwidth;
    private Double inputUtilization;
    private Double outputUtilization;
    private String status;
}