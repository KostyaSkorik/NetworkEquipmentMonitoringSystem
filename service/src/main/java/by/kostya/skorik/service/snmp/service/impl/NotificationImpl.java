package by.kostya.skorik.service.snmp.service.impl;

import by.kostya.skorik.domain.dto.Members;
import by.kostya.skorik.domain.dto.VkResponse;
import by.kostya.skorik.domain.dto.VkUser;
import by.kostya.skorik.domain.model.Alerts;
import by.kostya.skorik.service.snmp.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Objects;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationImpl implements NotificationService {

    private final RestClient restClient;

    private static final String baseUrl = "https://api.vk.com/method/";
    private static final String sendMessageMethod = "messages.send";
    private static final String getMembersMethod = "groups.getMembers";
    private static final String v = "5.199";

    @Value("${VK.VK_TOKEN}")
    private String VK_TOKEN;

    @Value("${VK.GROUP_ID}")
    private String GROUP_ID;

    public List<VkUser> getMembers() {
        String field = "site";
        String v = "5.199";
        ResponseEntity<VkResponse<Members>> response = restClient
                .get()
                .uri(baseUrl + getMembersMethod + "?group_id=%s&fields=%s&v=%s".formatted(GROUP_ID, field, v))
                .header("Authorization", "Bearer " + VK_TOKEN)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {
                });

        if (response.hasBody()) {
            return Objects.requireNonNull(response.getBody()).getResponse().getItems();
        }
        return List.of();
    }

    @Override
    public void sendAlerts(Alerts alerts) {
        String message = alerts.toString();
        List<VkUser> userList = getMembers();
        log.info("Message pre send {}", message);
        for (VkUser user : userList) {
            sendMessage(user.getId(), message);
        }
    }

    @Async
    public void sendMessage(Long vkUserId, String message) {
        Random random = new Random();
        String random_id = String.valueOf(random.nextInt());

        ResponseEntity<Void> response = restClient
                .get()
                .uri(baseUrl + sendMessageMethod
                     + "?user_id=%s&group_id=%s&random_id=%s&v=%s&message=%s"
                             .formatted(vkUserId, GROUP_ID, random_id, v, message))
                .header("Authorization", "Bearer " + VK_TOKEN)
                .retrieve()
                .toBodilessEntity();
        log.info("Response: {}", response.getStatusCode());
    }
}
