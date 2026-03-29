package by.kostya.skorik.service.snmp.trap;

import by.kostya.skorik.domain.model.Alerts;
import by.kostya.skorik.domain.model.Metrics;
import by.kostya.skorik.domain.model.Router;
import by.kostya.skorik.domain.ports.AlertPort;
import by.kostya.skorik.domain.ports.RouterPort;
import by.kostya.skorik.service.snmp.polling.SNMPPolling;
import by.kostya.skorik.service.snmp.service.NotificationService;
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
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlertListeningService implements CommandResponder {
    private final RouterPort routerPort;
    private final SNMPPolling snmpPolling;
    private final AlertPort alertPort;
    private final NotificationService notificationService;
    private TransportMapping<?> transport;
    private Snmp snmp;
    //TODO перевести в интерфейс
    private final MibStorage storageService;
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

        Alerts alerts = new Alerts();
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
                alerts.setTrapType(resolvedTrapType);
                String ip = commandResponderEvent.getPeerAddress().toString().split("/")[0];
                alerts.setIpSource(ip);
                alerts.setRouterName(routerPort.findByIp(ip).getName());
                alerts.setTime(LocalDateTime.now());
            }
            if (entryName.equals("sysUpTime")) {
                alerts.setSysUpTime(value);
            }
            if (entryName.equals("ifDescr")) {
                alerts.setInterfaceName(value);
            }
            if (oid.substring(0, oid.lastIndexOf(".")).equals("1.3.6.1.4.1.9.2.2.1.1.20")) {
                alerts.setMessage(value);
            }
            log.info("oid {} value {}", oid, value);

        }
        // TODO отправка в кафку
        if (alerts.getTrapType() != null) {
            log.info("Отправка DTO: {}", alerts);
            alertPort.save(alerts);
            notificationService.sendAlerts(alerts);
        }
    }

    public void checkUtilization(List<Metrics> metricsList, Router router) {
        log.info("Start check utilization");
        String sysUpTimeCache = null;
        for (Metrics metrics : metricsList) {
            Double inputUtilization = metrics.getInputUtilization();
            Double outputUtilization = metrics.getOutputUtilization();
            if (inputUtilization > 80 || outputUtilization > 80) {
                if (sysUpTimeCache == null) {
                    sysUpTimeCache = getSysUpTime(router.getIp());
                }
                Alerts alerts = new Alerts();
                alerts.setTime(LocalDateTime.now());
                alerts.setIpSource(router.getIp());
                alerts.setRouterName(router.getName());
                alerts.setTrapType("port overload");
                alerts.setSysUpTime(sysUpTimeCache);
                alerts.setInterfaceName(metrics.getInterfaceName());
                StringBuilder message = new StringBuilder("The port is overloaded.");
                if (inputUtilization > 80) message.append(" Input: ").append(inputUtilization).append("%");
                if (outputUtilization > 80) message.append(" Output: ").append(outputUtilization).append("%");
                alerts.setMessage(message.toString());

                // TODO отправка в кафку
                log.info("Отправка DTO: {}", alerts);
                alertPort.save(alerts);
                notificationService.sendAlerts(alerts);
            }
        }
        log.info("End check utilization");
    }

    public String getSysUpTime(String routerIp) {
        String ip = "udp:" + routerIp + "/161";
        try {
            return snmpPolling.get(new OID(storageService.getOid("sysUpTime")), ip);
        } catch (IOException e) {
            log.error("Failed to fetch sysUpTime for router {}", routerIp);
            return "Cannot set SysUpTime";
        }
    }
}