package by.kostya.skorik.service.snmp;

import org.snmp4j.smi.OID;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SNMPAll {
//    private static final String address = "udp:192.168.56.10/161";
//
//
//    public static void main(String[] args) throws IOException {
//        SNMPManager client = new SNMPManager(address);
//        TrapReceiver trapReceiver = new TrapReceiver();
//
//        client.start();
//
//        String sysName = client.getAsString(new OID("1.3.6.1.2.1.1.5.0"));
//        System.out.println("Устройство: " + sysName);
//        System.out.println(); // Пустая строка для красоты
//
//        CompletableFuture.runAsync(() -> {
//            try {
//                trapReceiver.listen();
//            } catch (IOException e) {
//                System.err.println("Ошибка Trap Receiver: " + e.getMessage());
//            }
//        });
//
//        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
//
//        scheduledExecutorService.scheduleAtFixedRate(client::getMetricsTable, 0, 20, TimeUnit.SECONDS);
//
//
//    }
}
