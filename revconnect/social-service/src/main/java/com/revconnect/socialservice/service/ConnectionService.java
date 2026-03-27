package com.revconnect.socialservice.service;

import com.revconnect.socialservice.dto.ConnectionDTO;
import com.revconnect.socialservice.entity.Connection;
import com.revconnect.socialservice.repository.ConnectionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ConnectionService {

    private final ConnectionRepository connectionRepository;

    public ConnectionService(ConnectionRepository connectionRepository) {
        this.connectionRepository = connectionRepository;
    }

    @Transactional
    public ConnectionDTO sendConnectionRequest(Long requesterId, Long receiverId) {
        if (requesterId.equals(receiverId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot send connection request to yourself");
        }

        Connection connection = new Connection();
        connection.setRequesterId(requesterId);
        connection.setReceiverId(receiverId);
        connection.setStatus("PENDING");

        Connection savedConnection = connectionRepository.save(connection);
        return convertToDTO(savedConnection);
    }

    @Transactional
    public ConnectionDTO acceptConnection(Long connectionId) {
        Connection connection = connectionRepository.findById(connectionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Connection not found"));

        if (!"PENDING".equals(connection.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Connection is not pending");
        }

        connection.setStatus("ACCEPTED");
        connection.setRespondedAt(LocalDateTime.now());

        Connection updatedConnection = connectionRepository.save(connection);
        return convertToDTO(updatedConnection);
    }

    @Transactional
    public ConnectionDTO rejectConnection(Long connectionId) {
        Connection connection = connectionRepository.findById(connectionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Connection not found"));

        if (!"PENDING".equals(connection.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Connection is not pending");
        }

        connection.setStatus("REJECTED");
        connection.setRespondedAt(LocalDateTime.now());

        Connection updatedConnection = connectionRepository.save(connection);
        return convertToDTO(updatedConnection);
    }

    @Transactional
    public void removeConnection(Long connectionId) {
        Connection connection = connectionRepository.findById(connectionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Connection not found"));
        connectionRepository.delete(connection);
    }

    public List<ConnectionDTO> getUserConnections(Long userId) {
        List<Connection> connections = connectionRepository.findAcceptedConnectionsByUserId(userId);
        return connections.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public List<ConnectionDTO> getPendingReceived(Long userId) {
        return connectionRepository.findPendingReceivedByUserId(userId)
                .stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public List<ConnectionDTO> getPendingSent(Long userId) {
        return connectionRepository.findPendingSentByUserId(userId)
                .stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    private ConnectionDTO convertToDTO(Connection connection) {
        ConnectionDTO dto = new ConnectionDTO();
        dto.setId(connection.getId());
        dto.setRequesterId(connection.getRequesterId());
        dto.setReceiverId(connection.getReceiverId());
        dto.setStatus(connection.getStatus());
        dto.setCreatedAt(connection.getCreatedAt());
        dto.setRespondedAt(connection.getRespondedAt());
        return dto;
    }
}
