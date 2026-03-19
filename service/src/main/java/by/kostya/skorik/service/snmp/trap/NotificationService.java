package by.kostya.skorik.service.snmp.trap;

import by.kostya.skorik.domain.dto.AlertDto;
import by.kostya.skorik.domain.ports.RouterPort;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService implements CommandResponder {
    private final RouterPort routerPort;
    private TransportMapping<?> transport;
    private Snmp snmp;

    private static final String OID_INTERFACE_NAME = "1.3.6.1.2.1.2.2.1.2";
    private static final String SNMP_TRAP_OID = "1.3.6.1.6.3.1.1.4.1.0";
    private static final String LINK_DOWN_OID = "1.3.6.1.6.3.1.1.5.3";
    private static final String LINK_UP_OID = "1.3.6.1.6.3.1.1.5.4";

    @Override
    public <A extends Address> void processPdu(CommandResponderEvent<A> commandResponderEvent) {
        PDU pdu = commandResponderEvent.getPDU();
        AlertDto alertDto = new AlertDto();
//        OIDTextFormat textFormat = new DictionaryOIDTextFormat();
//        if (pdu == null || (pdu.getType()!=PDU.TRAP) || (pdu.getType()!=PDU.V1TRAP)) {
//            return;
//        }
        alertDto.setIpSource(commandResponderEvent.getPeerAddress().toString().split("/")[0]);
        alertDto.setRouterName(routerPort.findByIp(alertDto.getIpSource()).getName());

        String typeTrap = "";
        for (VariableBinding vb : pdu.getVariableBindings()) {
            String oid = vb.getOid().toString();
            String value = vb.getVariable().toString();

            if(oid.equals(SNMP_TRAP_OID)) {
                typeTrap = value;
            }
//            log.info("oid {} value {}", textFormat.format(vb.getOid().getValue()), vb.getVariable());
        }
        log.info("Trap Type = {}", typeTrap);
        System.out.println(alertDto);

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
