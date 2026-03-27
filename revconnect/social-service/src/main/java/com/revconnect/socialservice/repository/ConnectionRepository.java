package com.revconnect.socialservice.repository;

import com.revconnect.socialservice.entity.Connection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConnectionRepository extends JpaRepository<Connection, Long> {
    List<Connection> findByRequesterId(Long requesterId);
    List<Connection> findByReceiverId(Long receiverId);
    List<Connection> findByStatus(String status);
    
    @Query("SELECT c FROM Connection c WHERE (c.requesterId = ?1 OR c.receiverId = ?1) AND c.status = 'ACCEPTED'")
    List<Connection> findAcceptedConnectionsByUserId(Long userId);

    @Query("SELECT c FROM Connection c WHERE c.receiverId = ?1 AND c.status = 'PENDING'")
    List<Connection> findPendingReceivedByUserId(Long userId);

    @Query("SELECT c FROM Connection c WHERE c.requesterId = ?1 AND c.status = 'PENDING'")
    List<Connection> findPendingSentByUserId(Long userId);
}
