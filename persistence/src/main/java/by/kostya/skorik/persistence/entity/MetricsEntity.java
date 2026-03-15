package by.kostya.skorik.persistence.entity;

import by.kostya.skorik.domain.model.InterfaceStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "metrics")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MetricsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "polling_time")
    private LocalDateTime pollingTime;

    @ManyToOne
    @JoinColumn(name = "router_id")
    private RouterEntity router;

    @Column(name = "interface_name")
    private String interfaceName;

    @Column(name = "input_counter")
    private Long inputCounter;

    @Column(name = "output_counter")
    private Long outputCounter;

    @Column(name = "input_bandwidth")
    private Double inputBandwidth;

    @Column(name = "output_bandwidth")
    private Double outputBandwidth;

    @Column(name = "input_utilization")
    private Double inputUtilization;

    @Column(name = "output_utilization")
    private Double outputUtilization;

    @Column(name = "status")
    @Enumerated(value = EnumType.STRING)
    private InterfaceStatus status;
}
