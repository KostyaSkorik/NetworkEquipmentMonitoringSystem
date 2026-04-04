package by.kostya.skorik.service.snmp.service.impl;

import by.kostya.skorik.domain.dto.VkUser;
import by.kostya.skorik.domain.model.Alerts;
import by.kostya.skorik.service.snmp.service.NotificationService;
import by.kostya.skorik.service.snmp.service.VkMethods;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationImpl implements NotificationService {
    private final VkMethods vkMethods;

    @Override
    public void sendAlerts(Alerts alerts) {
        String message = formatVkMessage(alerts);
        List<VkUser> userList = vkMethods.getMembers();
        log.info("Message pre send {}", message);
        for (VkUser user : userList) {
            vkMethods.sendMessage(user.getId(), message);
        }
    }


    private String formatVkMessage(Alerts alert) {
        if (alert == null) {
            return "⚠️ Получен пустой алерт.";
        }

        StringBuilder sb = new StringBuilder();

        String trapType = alert.getTrapType() != null ? alert.getTrapType() : "Unknown";
        String headerIcon = "🚨";
        String headerText = "СЕТЕВОЙ ИНЦИДЕНТ";

        if (trapType.equalsIgnoreCase("linkUp")) {
            headerIcon = "✅";
            headerText = "ВОССТАНОВЛЕНИЕ СЕТИ";
        } else if (trapType.toLowerCase().contains("overload")) {
            headerIcon = "⚠️";
            headerText = "ПРЕДУПРЕЖДЕНИЕ О НАГРУЗКЕ";
        }
        sb.append(headerIcon).append(" [").append(headerText).append("] ").append(headerIcon).append("\n\n");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
        String formattedTime = alert.getTime() != null ? alert.getTime().format(formatter) : "Неизвестно";
        sb.append("🕒 Время: ").append(formattedTime).append("\n");

        String routerName = alert.getRouterName() != null ? alert.getRouterName() : "Неизвестный узел";
        String ipSource = alert.getIpSource() != null ? alert.getIpSource() : "IP неизвестен";
        sb.append("📍 Узел: ").append(routerName).append(" (").append(ipSource).append(")\n");

        if (alert.getInterfaceName() != null && !alert.getInterfaceName().isEmpty()) {
            sb.append("🔌 Интерфейс: ").append(alert.getInterfaceName()).append("\n");
        }
        sb.append("⚙️ Событие: ").append(trapType).append("\n");
        if (alert.getSysUpTime() != null && !alert.getSysUpTime().isEmpty()) {
            sb.append("⏱ SysUpTime: ").append(alert.getSysUpTime()).append("\n");
        }

        if (alert.getMessage() != null && !alert.getMessage().isEmpty()) {
            sb.append("\n💬 Детали:\n").append(alert.getMessage());
        }

        return sb.toString();
    }
}