package by.kostya.skorik.service.snmp.trap;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import net.percederberg.mibble.*;
import org.snmp4j.smi.OID;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
public class MibStorage {
    MibLoader loader = new MibLoader();

    @PostConstruct
    public void loader() {
        String[] standardMibs = {
                "SNMPv2-SMI",
                "SNMPv2-MIB",
                "SNMPv2-CONF",
                "SNMPv2-TC",
                "IF-MIB",
                "IP-MIB",
                "UDP-MIB",
                "TCP-MIB",
                "SNMP-FRAMEWORK-MIB",
                "SNMPv2-TM",
                "OSPF-MIB"

        };
        for (String mib : standardMibs) {
            try {
                loader.load(mib);
            } catch (IOException | MibLoaderException e) {
                throw new RuntimeException(e);
            }
        }

    }

    public String getName(String oid) {
        if (oid == null || oid.isEmpty()) return "unknown";
        String currentOid = oid.startsWith(".") ? oid.substring(1) : oid;
        while (currentOid.contains(".")) {
            for (Mib m : loader.getAllMibs()) {
                MibValueSymbol symbol = m.getSymbolByValue(currentOid);
                if (symbol != null) {
                    return symbol.getName();
                }
            }
            int lastDotIndex = currentOid.lastIndexOf('.');
            if (lastDotIndex == -1) break;
            currentOid = currentOid.substring(0, lastDotIndex);
        }
        return oid;
    }

    public String getOid(String name) {
        String oid = null;
        for (Mib m : loader.getAllMibs()) {
            MibSymbol s = m.getSymbol(name);
            if (s instanceof MibValueSymbol) {
                oid = ((MibValueSymbol) s).getValue().toString();
                if (((MibValueSymbol) s).isScalar()) {
                    oid = new OID(oid).append(0).toDottedString();
                }
            }
        }
        return oid;
    }
}
