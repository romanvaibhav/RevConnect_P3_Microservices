package com.revconnect.socialservice.controller;

import com.revconnect.socialservice.dto.ConnectionDTO;
import com.revconnect.socialservice.dto.ConnectionRequest;
import com.revconnect.socialservice.service.ConnectionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/connections")
public class ConnectionController {

    private final ConnectionService connectionService;

    public ConnectionController(ConnectionService connectionService) {
        this.connectionService = connectionService;
    }

    @PostMapping("/request")
    public ResponseEntity<ConnectionDTO> sendConnectionRequest(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody ConnectionRequest request) {
        ConnectionDTO connection = connectionService.sendConnectionRequest(userId, request.getReceiverId());
        return ResponseEntity.status(HttpStatus.CREATED).body(connection);
    }

    @PutMapping("/{connectionId}/accept")
    public ResponseEntity<ConnectionDTO> acceptConnection(@PathVariable Long connectionId) {
        ConnectionDTO connection = connectionService.acceptConnection(connectionId);
        return ResponseEntity.ok(connection);
    }

    @PutMapping("/{connectionId}/reject")
    public ResponseEntity<ConnectionDTO> rejectConnection(@PathVariable Long connectionId) {
        ConnectionDTO connection = connectionService.rejectConnection(connectionId);
        return ResponseEntity.ok(connection);
    }

    @DeleteMapping("/{connectionId}")
    public ResponseEntity<Void> removeConnection(@PathVariable Long connectionId) {
        connectionService.removeConnection(connectionId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ConnectionDTO>> getUserConnections(@PathVariable Long userId) {
        List<ConnectionDTO> connections = connectionService.getUserConnections(userId);
        return ResponseEntity.ok(connections);
    }

    @GetMapping("/pending/received")
    public ResponseEntity<List<ConnectionDTO>> getPendingReceived(@RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(connectionService.getPendingReceived(userId));
    }

    @GetMapping("/pending/sent")
    public ResponseEntity<List<ConnectionDTO>> getPendingSent(@RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(connectionService.getPendingSent(userId));
    }
}
