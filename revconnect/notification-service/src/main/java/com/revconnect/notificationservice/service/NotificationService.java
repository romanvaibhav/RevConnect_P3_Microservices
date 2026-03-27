package com.revconnect.notificationservice.service;

import com.revconnect.notificationservice.dto.CreateNotificationRequest;
import com.revconnect.notificationservice.dto.NotificationDTO;
import com.revconnect.notificationservice.entity.Notification;
import com.revconnect.notificationservice.repository.NotificationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private static final List<String> VALID_TYPES = Arrays.asList("LIKE", "COMMENT", "FOLLOW", "CONNECTION_REQUEST", "SHARE");

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Transactional
    public NotificationDTO createNotification(CreateNotificationRequest request) {
        if (!VALID_TYPES.contains(request.getType().toUpperCase())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Invalid notification type. Must be one of: " + String.join(", ", VALID_TYPES));
        }

        Notification notification = new Notification();
        notification.setSenderId(request.getSenderId());
        notification.setReceiverId(request.getReceiverId());
        notification.setType(request.getType().toUpperCase());
        notification.setPostId(request.getPostId());
        notification.setMessage(request.getMessage());
        notification.setRead(false);

        Notification savedNotification = notificationRepository.save(notification);
        return convertToDTO(savedNotification);
    }

    public Page<NotificationDTO> getUserNotifications(Long userId, Pageable pageable) {
        Page<Notification> notifications = notificationRepository.findByReceiverIdOrderByCreatedAtDesc(userId, pageable);
        return notifications.map(this::convertToDTO);
    }

    public List<NotificationDTO> getUnreadNotifications(Long userId) {
        List<Notification> notifications = notificationRepository.findByReceiverIdAndIsReadFalse(userId);
        return notifications.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Transactional
    public NotificationDTO markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification not found"));
        
        notification.setRead(true);
        Notification updatedNotification = notificationRepository.save(notification);
        return convertToDTO(updatedNotification);
    }

    @Transactional
    public void deleteNotification(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification not found"));
        notificationRepository.delete(notification);
    }

    @Transactional
    public void markAllAsRead(Long userId) {
        List<Notification> unread = notificationRepository.findByReceiverIdAndIsReadFalse(userId);
        unread.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(unread);
    }

    public long getUnreadCount(Long userId) {
        return notificationRepository.findByReceiverIdAndIsReadFalse(userId).size();
    }

    private NotificationDTO convertToDTO(Notification notification) {
        NotificationDTO dto = new NotificationDTO();
        dto.setId(notification.getId());
        dto.setSenderId(notification.getSenderId());
        dto.setReceiverId(notification.getReceiverId());
        dto.setType(notification.getType());
        dto.setPostId(notification.getPostId());
        dto.setMessage(notification.getMessage());
        dto.setRead(notification.isRead());
        dto.setCreatedAt(notification.getCreatedAt());
        return dto;
    }
}
