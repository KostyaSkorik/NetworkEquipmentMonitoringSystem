package by.kostya.skorik.service.snmp.polling;

import org.snmp4j.smi.OID;
import org.snmp4j.util.TableEvent;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface SNMPPolling {

    void start() throws IOException;

    void stop() throws IOException;

    CompletableFuture<List<TableEvent>> getMetricsTable(String ipAddress);

    String get(OID oid, String ipAddress) throws IOException;

}