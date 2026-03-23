package by.kostya.skorik.domain.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AlertsDto {
    private LocalDateTime time;
    private String ipSource;
    private String routerName;
    private String trapType;
    private String sysUpTime;
    private String interfaceName;
    private String message;
}