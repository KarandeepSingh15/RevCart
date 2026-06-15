package com.revcart.common_outbox.repository;

import com.revcart.common_outbox.entity.OutboxEvent;
import com.revcart.common_outbox.enums.OutboxStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OutboxRepository extends JpaRepository<OutboxEvent, Long> {

    List<OutboxEvent> findByStatus(OutboxStatus status);

    @Query("""
            select o
            from OutboxEvent o
            where o.status in ('PENDING', 'FAILED')
            """)
    List<OutboxEvent> findPendingEvents();

}
