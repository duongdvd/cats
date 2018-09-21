package jp.co.willwave.aca.web.controller;

import jp.co.willwave.aca.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/notifications")
    public ResponseEntity getNotifications() {
        return ResponseEntity.ok(notificationService.findNotification());
    }

    @GetMapping("/messages")
    public ResponseEntity getMessages() {
        return ResponseEntity.ok(notificationService.findMessage());
    }
}
