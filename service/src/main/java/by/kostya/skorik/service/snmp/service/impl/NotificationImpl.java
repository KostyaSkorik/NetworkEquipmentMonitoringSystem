package by.kostya.skorik.service.snmp.service.impl;

import by.kostya.skorik.domain.dto.VkUser;
import by.kostya.skorik.domain.model.Alerts;
import by.kostya.skorik.service.snmp.service.NotificationService;
import by.kostya.skorik.service.snmp.service.VkMethods;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationImpl implements NotificationService {
    private final VkMethods vkMethods;

    @Override
    public void sendAlerts(Alerts alerts) {
        String message = alerts.toString();
        List<VkUser> userList = vkMethods.getMembers();
        log.info("Message pre send {}", message);
        for (VkUser user : userList) {
            vkMethods.sendMessage(user.getId(), message);
        }
    }
}