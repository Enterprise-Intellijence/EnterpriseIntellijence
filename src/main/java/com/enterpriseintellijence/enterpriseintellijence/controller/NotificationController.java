package com.enterpriseintellijence.enterpriseintellijence.controller;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.Notification;
import com.enterpriseintellijence.enterpriseintellijence.data.services.NotificationService;
import com.enterpriseintellijence.enterpriseintellijence.dto.NotificationDTO;
import com.nimbusds.jose.JOSEException;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.enterpriseintellijence.enterpriseintellijence.security.AppSecurityConfig.SECURITY_CONFIG_NAME;

@RestController
@RequiredArgsConstructor
@RequestMapping(path="/api/v1/notifications", produces="application/json")
@CrossOrigin(origins="http://localhost:4200")
@Slf4j
@SecurityRequirement(name = SECURITY_CONFIG_NAME)
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping(path = "/" )
    public ResponseEntity<Page<NotificationDTO>> getAllUserNotifications(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int sizePage) throws JOSEException {
        return new ResponseEntity<>(notificationService.getAllUserNotifications(page, sizePage), HttpStatus.OK);
    }

    @PostMapping(path = "/")
    public ResponseEntity<NotificationDTO> readNotification(@RequestBody NotificationDTO notificationDTO) {
        return new ResponseEntity<>(notificationService.readNotification(notificationDTO), HttpStatus.OK);
    }
}
