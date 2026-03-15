package by.kostya.skorik.service.snmp.polling;

import org.snmp4j.util.TableEvent;

import java.io.IOException;
import java.util.List;

public interface SNMPPolling {

    void start() throws IOException;

    void stop() throws IOException;

    public List<TableEvent> getMetricsTable(String ipAddress);

}