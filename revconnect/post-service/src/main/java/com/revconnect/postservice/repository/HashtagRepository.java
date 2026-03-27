package com.revconnect.postservice.repository;

import com.revconnect.postservice.entity.Hashtag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HashtagRepository extends JpaRepository<Hashtag, Long> {
    Optional<Hashtag> findByName(String name);

    Optional<Hashtag> findByNameIgnoreCase(String name);

    @Query("SELECT h FROM Hashtag h ORDER BY h.usageCount DESC")
    List<Hashtag> findTopByOrderByUsageCountDesc(org.springframework.data.domain.Pageable pageable);
}
