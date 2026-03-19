package by.kostya.skorik.service.snmp.polling;

import jakarta.annotation.PreDestroy;
import org.snmp4j.*;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.DefaultPDUFactory;
import org.snmp4j.util.TableEvent;
import org.snmp4j.util.TableUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class SNMPPollingImpl implements SNMPPolling {
    private Snmp snmp = null;
    private TransportMapping<? extends Address> transport = null;
    @Value("${community.string}")
    private String communityString;

    @Override
    @EventListener(ApplicationReadyEvent.class)
    public void start() throws IOException {
        transport = new DefaultUdpTransportMapping();
        snmp = new Snmp(transport);
        transport.listen();
    }

    @Override
    @PreDestroy
    public void stop() throws IOException {
        if (snmp != null) snmp.close();
        if (transport != null) transport.close();
    }

//    public String getAsString(OID oid) throws IOException {
//        ResponseEvent<Address> event = get(oid);
//        return event.getResponse().get(0).getVariable().toString();
//    }

//    public ResponseEvent<Address> get(OID oid) throws IOException {
//        PDU pdu = new PDU();
//        pdu.addOID(new VariableBinding(oid));
//        pdu.setType(PDU.GET);
//        ResponseEvent<Address> event = snmp.send(pdu, getTarget());
//        if (event != null) {
//            return event;
//        }
//        throw new RuntimeException("GET timed out");
//    }

    private Target<Address> getTarget(String ipAddress) {
        Address targetAddress = GenericAddress.parse(ipAddress);
        CommunityTarget<Address> target = new CommunityTarget<>();
        target.setCommunity(new OctetString(communityString));
        target.setAddress(targetAddress);
        target.setRetries(2);
        target.setTimeout(1500);
        target.setVersion(SnmpConstants.version2c);
        return target;
    }

    @Override
    public List<TableEvent> getMetricsTable(String ipAddress) {
        // Создаем утилиту для работы с таблицами. GETBULK работает быстрее и эффективнее.
        TableUtils tableUtils = new TableUtils(snmp, new DefaultPDUFactory(PDU.GETBULK));

        // Массив колонок, которые мы хотим вытащить (OID без индексов портов на конце!)
        OID[] columnOIDs = new OID[]{
//                new OID("1.3.6.1.2.1.2.2.1.1"),  // 0: ifIndex (Номер порта)
                new OID("1.3.6.1.2.1.2.2.1.2"),  // 0: ifDescr (Имя интерфейса)
                new OID("1.3.6.1.2.1.2.2.1.10"), // 1: ifInOctets (Входящие байты)
                new OID("1.3.6.1.2.1.2.2.1.16"), // 2: ifOutOctets (Исходящие байты)
                new OID("1.3.6.1.2.1.2.2.1.8"),  // 3: ifOperStatus (Текущий статус)
                new OID("1.3.6.1.2.1.2.2.1.5")   // 4: ifSpeed(максимальная скорость)
        };

        // Запрашиваем таблицу
        return tableUtils.getTable(getTarget(ipAddress), columnOIDs, null, null);
    }
}
