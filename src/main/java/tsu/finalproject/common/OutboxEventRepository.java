package tsu.finalproject.common;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OutboxEventRepository extends JpaRepository<OutboxEvent, Long> {


    @Query(value = "SELECT * FROM outbox_events " +
                           "WHERE status IN ('PENDING', 'FAILED') AND retry_count < 3 " +
                           "ORDER BY created_at FOR UPDATE SKIP LOCKED LIMIT :limit",
            nativeQuery = true)
    List<OutboxEvent> findPendingEventsForProcessing(@Param("limit") int limit);

    @Modifying
    @Query("DELETE FROM OutboxEvent o WHERE o.status = 'COMPLETED' AND o.processedAt < :retentionThreshold")
    void deleteCompletedOutboxEvents(@Param("retentionThreshold") LocalDateTime retentionThreshold);
}