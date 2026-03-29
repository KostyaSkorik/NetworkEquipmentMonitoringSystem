package by.kostya.skorik.service.snmp.service;

import by.kostya.skorik.domain.dto.VkUser;

import java.util.List;

public interface VkMethods {
    List<VkUser> getMembers();
    void sendMessage(Long vkUserId, String message);
}
