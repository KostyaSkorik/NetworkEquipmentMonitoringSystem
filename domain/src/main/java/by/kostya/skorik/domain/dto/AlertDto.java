package by.kostya.skorik.domain.dto;

import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@ToString
public class AlertDto {
    private LocalDateTime time;
    private String ipSource;
    private String routerName;
    private String trapType;
    private String sysUpTime;
    private String interfaceName;
    private String message;
}
