package by.kostya.skorik.service.snmp.trap;

import org.snmp4j.*;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import java.io.IOException;

public class TrapReceiver implements CommandResponder {
    @Override
    public <A extends Address> void processPdu(CommandResponderEvent<A> commandResponderEvent) {
        PDU pdu = commandResponderEvent.getPDU();
        if(pdu != null){
            System.out.println("ATTENTION TRAP!!!");
            System.out.println("-".repeat(50));
            System.out.println("Отправитель: " + commandResponderEvent.getPeerAddress());
            System.out.println("Тип события: " + PDU.getTypeString(pdu.getType()));

            for(VariableBinding vb : pdu.getVariableBindings()){
                System.out.println(vb.getOid() + " " + vb.getVariable());
            }
        }
    }


    public void listen() throws IOException {
        TransportMapping<?> transport = new DefaultUdpTransportMapping(new UdpAddress("0.0.0.0/1162"));
        Snmp snmp = new Snmp(transport);

        snmp.addCommandResponder(this);
        transport.listen();

        System.out.println("Trap Receiver запущен и слушает порт 162...");
    }
}
