package by.kostya.skorik.service.snmp.polling;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.snmp4j.*;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.DefaultPDUFactory;
import org.snmp4j.util.TableEvent;
import org.snmp4j.util.TableUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
@RequiredArgsConstructor
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

    public String get(OID oid, String ipAddress) throws IOException {
        PDU pdu = new PDU();
        pdu.addOID(new VariableBinding(oid));
        pdu.setType(PDU.GET);
        ResponseEvent<Address> event = snmp.send(pdu, getTarget(ipAddress));
        if (event != null) {
            return event.getResponse().get(0).getVariable().toString();
        }
        throw new RuntimeException("GET timed out");
    }


    private Target<Address> getTarget(String ipAddress) {
        Address targetAddress = GenericAddress.parse(ipAddress);
        CommunityTarget<Address> target = new CommunityTarget<>();
        target.setCommunity(new OctetString(communityString));
        target.setAddress(targetAddress);
        target.setRetries(2);
        target.setTimeout(3000);
        target.setVersion(SnmpConstants.version2c);
        return target;
    }


    @Override
    @Async("SnmpExecutor")
    public CompletableFuture<List<TableEvent>> getMetricsTable(String ipAddress) {
        log.info("Поток {} в методе getMetrics", Thread.currentThread().getName());
        // Создаем утилиту для работы с таблицами. GETBULK работает быстрее и эффективнее.
        TableUtils tableUtils = new TableUtils(snmp, new DefaultPDUFactory(PDU.GETBULK));

        // Массив колонок, которые мы хотим вытащить (OID без индексов портов на конце!)
        OID[] columnOIDs = new OID[]{
                new OID("1.3.6.1.2.1.2.2.1.2"),  // 0: ifDescr (Имя интерфейса)
                new OID("1.3.6.1.2.1.2.2.1.10"), // 1: ifInOctets (Входящие байты)
                new OID("1.3.6.1.2.1.2.2.1.16"), // 2: ifOutOctets (Исходящие байты)
                new OID("1.3.6.1.2.1.2.2.1.8"),  // 3: ifOperStatus (Текущий статус)
                new OID("1.3.6.1.2.1.2.2.1.5")   // 4: ifSpeed(максимальная скорость)
        };

        Target<Address> target = getTarget(ipAddress);

        List<TableEvent> tableEvents = tableUtils.getTable(target, columnOIDs, null, null);
        // Запрашиваем таблицу
        if(tableEvents.get(0).getStatus()==-1){
            return CompletableFuture.failedFuture(new RuntimeException("Router is unreachable"));
        }
        return CompletableFuture.completedFuture(tableEvents);
    }
}
