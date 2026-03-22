package by.kostya.skorik.service.snmp.trap;

import by.kostya.skorik.domain.dto.AlertDto;
import by.kostya.skorik.domain.ports.RouterPort;
import by.kostya.skorik.service.snmp.MibStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.snmp4j.*;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService implements CommandResponder {
    private final RouterPort routerPort;
    private TransportMapping<?> transport;
    private Snmp snmp;
    private final MibStorageService storageService;


    @Override
    public <A extends Address> void processPdu(CommandResponderEvent<A> commandResponderEvent) {
        PDU pdu = commandResponderEvent.getPDU();
        if (pdu == null) return;

        AlertDto alertDto = new AlertDto();
        String ip = commandResponderEvent.getPeerAddress().toString().split("/")[0];
        alertDto.setIpSource(ip);
        alertDto.setRouterName(routerPort.findByIp(ip).getName());
        alertDto.setTime(LocalDateTime.now());

        String resolvedTrapType = "";

        for (VariableBinding vb : pdu.getVariableBindings()) {
            String oid = vb.getOid().toString();
            String value = vb.getVariable().toString();

            String entryName = storageService.getName(oid);
            if ("snmpTrapOID".equals(storageService.getName(oid))) {
                resolvedTrapType = storageService.getName(value);
//                alertDto.setTrapType(TrapType.valueOf(resolvedTrapType));

            }
            log.info("clear oid {} named_oid {} value {}", oid, entryName, value);
        }
        // TODO сохранение в бд и отправка в кафку
        if (alertDto.getTrapType() != null) {
            log.info("Отправка DTO: {}", alertDto);
        }


    }

    @EventListener(ApplicationReadyEvent.class)
    public void listen() throws IOException {
        transport = new DefaultUdpTransportMapping(new UdpAddress("0.0.0.0/1162"));
        snmp = new Snmp(transport);
        snmp.addCommandResponder(this);
        snmp.listen();
        log.info("listening port 1162....");
    }
}
