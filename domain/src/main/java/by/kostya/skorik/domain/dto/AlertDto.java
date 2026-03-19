package by.kostya.skorik.domain.dto;

import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@ToString
public class AlertDto {
    private String ipSource;
    private String routerName;
    private TrapType trapType;
    private LocalDateTime time;
//    private Map<String,String> otherInfo;
}
