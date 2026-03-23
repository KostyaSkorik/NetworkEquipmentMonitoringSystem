package by.kostya.skorik.service.snmp.trap;

import by.kostya.skorik.domain.dto.AlertDto;
import by.kostya.skorik.domain.model.Metrics;
import by.kostya.skorik.domain.model.Router;
import by.kostya.skorik.domain.ports.AlertPort;
import by.kostya.skorik.domain.ports.MetricsPort;
import by.kostya.skorik.domain.ports.RouterPort;
import by.kostya.skorik.service.snmp.MibStorageService;
import by.kostya.skorik.service.snmp.polling.SNMPPolling;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.snmp4j.*;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlertListeningService implements CommandResponder {
    private final RouterPort routerPort;
    private final MetricsPort metricsPort;
    private final SNMPPolling snmpPolling;
    private final AlertPort alertPort;
    private TransportMapping<?> transport;
    private Snmp snmp;
    private final MibStorageService storageService;
    private static final List<String> possibleTraps = List.of("linkDown", "linkUp");

    @EventListener(ApplicationReadyEvent.class)
    public void listen() throws IOException {
        transport = new DefaultUdpTransportMapping(new UdpAddress("0.0.0.0/1162"));
        snmp = new Snmp(transport);
        snmp.addCommandResponder(this);
        snmp.listen();
        log.info("listening port 1162....");
    }


    @Override
    public <A extends Address> void processPdu(CommandResponderEvent<A> commandResponderEvent) {
        PDU pdu = commandResponderEvent.getPDU();
        if (pdu == null) return;

        AlertDto alertDto = new AlertDto();


        String resolvedTrapType;

        for (VariableBinding vb : pdu.getVariableBindings()) {
            String oid = vb.getOid().toString();
            String value = vb.getVariable().toString();

            String entryName = storageService.getName(oid);
            if ("snmpTrapOID".equals(storageService.getName(oid))) {
                resolvedTrapType = storageService.getName(value);
                if (!possibleTraps.contains(resolvedTrapType)) {
                    break;
                }
                alertDto.setTrapType(resolvedTrapType);
                String ip = commandResponderEvent.getPeerAddress().toString().split("/")[0];
                alertDto.setIpSource(ip);
                alertDto.setRouterName(routerPort.findByIp(ip).getName());
                alertDto.setTime(LocalDateTime.now());
            }
            if (entryName.equals("sysUpTime")) {
                alertDto.setSysUpTime(value);
            }
            if (entryName.equals("ifDescr")) {
                alertDto.setInterfaceName(value);
            }
            if (oid.equals("1.3.6.1.4.1.9.2.2.1.1.20.3")) {
                alertDto.setMessage(value);
            }

        }
        // TODO сохранение в бд и отправка в кафку
        if (alertDto.getTrapType() != null) {
            log.info("Отправка DTO: {}", alertDto);
            alertPort.save(alertDto);
        }
    }

    @Scheduled(fixedRate = 30000)
    public void checkUtilization() throws IOException {
        List<Metrics> metricsList = metricsPort.getLastMetrics();
        AlertDto alertDto;
        for (Metrics metrics : metricsList) {
            Double inputUtilization = metrics.getInputUtilization();
            Double outputUtilization = metrics.getOutputUtilization();
            if (inputUtilization > 80 || outputUtilization > 80) {
                alertDto = new AlertDto();
                Router router = routerPort.getRouterById(metrics.getRouterId());
                String ip = "udp:" + router.getIp() + "/161";
                alertDto.setTime(LocalDateTime.now());
                alertDto.setIpSource(router.getIp());
                alertDto.setRouterName(router.getName());
                alertDto.setTrapType("port overload");
                alertDto.setSysUpTime(snmpPolling.get(new OID(storageService.getOid("sysUpTime")), ip));
                alertDto.setInterfaceName(metrics.getInterfaceName());
                if (inputUtilization > 80) {
                    alertDto.setMessage("The port is overloaded. Input Utilization: " + inputUtilization);
                }
                if (outputUtilization > 80) {
                    alertDto.setMessage("The port is overloaded. Output Utilization: " + outputUtilization);
                }
                // TODO сохранение в бд и отправка в кафку
                log.info("Отправка DTO: {}", alertDto);
                alertPort.save(alertDto);
            }
        }
    }
}