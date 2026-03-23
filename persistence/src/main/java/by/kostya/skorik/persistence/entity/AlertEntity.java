package by.kostya.skorik.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "alerts")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlertEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime time;

    @Column(name = "ip_source")
    private String ipSource;

    @Column(name = "router_name")
    private String routerName;

    @Column(name = "trap_type")
    private String trapType;

    @Column(name = "sys_up_time")
    private String sysUpTime;

    @Column(name = "interface_name")
    private String interfaceName;

    private String message;
}