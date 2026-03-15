package by.kostya.skorik.service.snmp.polling;

import org.snmp4j.*;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.DefaultPDUFactory;
import org.snmp4j.util.TableEvent;
import org.snmp4j.util.TableUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class SNMPPollingImpl implements SNMPPolling {
    private Snmp snmp = null;
    private TransportMapping<? extends Address> transport = null;


    public void start() throws IOException {
        transport = new DefaultUdpTransportMapping();
        snmp = new Snmp(transport);
        transport.listen();
    }

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
        target.setCommunity(new OctetString("R3"));
        target.setAddress(targetAddress);
        target.setRetries(2);
        target.setTimeout(1500);
        target.setVersion(SnmpConstants.version2c);
        return target;
    }

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

//        // Рисуем красивую шапку для консоли
//        System.out.printf("%-5s | %-25s | %-6s | %-15s | %-15s%n", "Index", "Interface Name", "Status", "In (Bytes)", "Out (Bytes)");
//        System.out.println("-".repeat(75));
//
//        // Перебираем каждую строку таблицы (каждый порт)
//        for (TableEvent event : events) {
//            if (event.isError()) {
//                System.err.println("Ошибка при получении строки: " + event.getErrorMessage());
//                continue;
//            }
//
//            // Массив значений колонок для конкретного порта
//            VariableBinding[] columns = event.getColumns();
//            if (columns == null || columns.length == 0) continue;
//
//            // Извлекаем данные, проверяя на null (иногда некоторые OID не поддерживаются)
//            String index = columns[0] != null ? columns[0].getVariable().toString() : "-";
//            String name = columns[1] != null ? columns[1].getVariable().toString() : "-";
//            String statusRaw = columns[2] != null ? columns[2].getVariable().toString() : "-";
//            String inBytes = columns[3] != null ? columns[3].getVariable().toString() : "0";
//            String outBytes = columns[4] != null ? columns[4].getVariable().toString() : "0";
//
//            // Превращаем цифры статуса в понятный текст
//            String statusFormatted = "1".equals(statusRaw) ? "UP" : ("2".equals(statusRaw) ? "DOWN" : statusRaw);
//
//            // Печатаем строку
//            System.out.printf("%-5s | %-25s | %-6s | %-15s | %-15s%n", index, name, statusFormatted, inBytes, outBytes);
//        }

    }
}
