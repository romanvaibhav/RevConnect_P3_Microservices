package com.revconnect.notificationservice.service;

import com.revconnect.notificationservice.dto.CreateNotificationRequest;
import com.revconnect.notificationservice.dto.NotificationDTO;
import com.revconnect.notificationservice.entity.Notification;
import com.revconnect.notificationservice.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationService notificationService;

    private Notification mockNotification;

    @BeforeEach
    void setUp() {
        mockNotification = new Notification();
        mockNotification.setId(1L);
        mockNotification.setSenderId(1L);
        mockNotification.setReceiverId(2L);
        mockNotification.setType("LIKE");
        mockNotification.setMessage("Someone liked your post");
        mockNotification.setRead(false);
    }

    @Test
    void createNotification_shouldSave_whenTypeIsValid() {
        when(notificationRepository.save(any(Notification.class))).thenReturn(mockNotification);

        CreateNotificationRequest req = new CreateNotificationRequest();
        req.setSenderId(1L);
        req.setReceiverId(2L);
        req.setType("LIKE");
        req.setMessage("Someone liked your post");

        NotificationDTO result = notificationService.createNotification(req);
        assertNotNull(result);
        assertEquals("LIKE", result.getType());
        verify(notificationRepository).save(any(Notification.class));
    }

    @Test
    void createNotification_shouldThrow_whenTypeIsInvalid() {
        CreateNotificationRequest req = new CreateNotificationRequest();
        req.setSenderId(1L);
        req.setReceiverId(2L);
        req.setType("INVALID_TYPE");
        req.setMessage("test");

        assertThrows(Exception.class, () -> notificationService.createNotification(req));
        verify(notificationRepository, never()).save(any());
    }

    @Test
    void getUnreadNotifications_shouldReturnUnreadOnly() {
        when(notificationRepository.findByReceiverIdAndIsReadFalse(2L))
                .thenReturn(List.of(mockNotification));

        List<NotificationDTO> result = notificationService.getUnreadNotifications(2L);
        assertEquals(1, result.size());
        assertFalse(result.get(0).isRead());
    }

    @Test
    void markAsRead_shouldSetReadTrue() {
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(mockNotification));
        when(notificationRepository.save(any())).thenReturn(mockNotification);

        NotificationDTO result = notificationService.markAsRead(1L);
        assertTrue(mockNotification.isRead());
        verify(notificationRepository).save(mockNotification);
    }

    @Test
    void markAsRead_shouldThrow_whenNotFound() {
        when(notificationRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(Exception.class, () -> notificationService.markAsRead(99L));
    }

    @Test
    void deleteNotification_shouldDelete_whenExists() {
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(mockNotification));
        notificationService.deleteNotification(1L);
        verify(notificationRepository).delete(mockNotification);
    }

    @Test
    void deleteNotification_shouldThrow_whenNotFound() {
        when(notificationRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(Exception.class, () -> notificationService.deleteNotification(99L));
    }

    @Test
    void markAllAsRead_shouldMarkAllUnread() {
        Notification n2 = new Notification();
        n2.setId(2L);
        n2.setRead(false);
        when(notificationRepository.findByReceiverIdAndIsReadFalse(2L))
                .thenReturn(List.of(mockNotification, n2));

        notificationService.markAllAsRead(2L);
        assertTrue(mockNotification.isRead());
        assertTrue(n2.isRead());
        verify(notificationRepository).saveAll(anyList());
    }

    @Test
    void getUnreadCount_shouldReturnCorrectCount() {
        when(notificationRepository.findByReceiverIdAndIsReadFalse(2L))
                .thenReturn(List.of(mockNotification));
        assertEquals(1L, notificationService.getUnreadCount(2L));
    }
}
