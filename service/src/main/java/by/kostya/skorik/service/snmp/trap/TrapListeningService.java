package by.kostya.skorik.service.snmp.trap;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class TrapListeningService {

    @EventListener(ApplicationReadyEvent.class)
    public void startListening() throws IOException {
        TrapReceiver trapReceiver = new TrapReceiver();
        trapReceiver.listen();
    }
}
