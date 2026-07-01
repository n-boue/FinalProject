package tsu.finalproject.common;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tsu.finalproject.feature.storage.FileStorageService;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxRelayScheduler {

    private final OutboxEventRepository outboxEventRepository;
    private final FileStorageService fileStorageService;
    private final Clock clock;

    @Scheduled(fixedDelay = 30000) // every 30 seconds
    @Transactional
    public void processOutboxEvents() {
        List<OutboxEvent> pendingEvents = outboxEventRepository.findPendingEventsForProcessing(50);

        if (pendingEvents.isEmpty()) {
            return;
        }

        for (OutboxEvent event : pendingEvents) {
            try {
                if ("FILE_DELETION".equals(event.getEventType())) {
                    fileStorageService.deleteFile(event.getPayload());
                }

                event.setStatus(OutboxStatus.COMPLETED);
                event.setProcessedAt(LocalDateTime.now(clock));
                log.debug("Successfully processed outbox event ID: {}", event.getId());

            } catch (Exception e) {
                event.setRetryCount(event.getRetryCount() + 1);
                event.setStatus(OutboxStatus.FAILED);
                event.setProcessedAt(LocalDateTime.now(clock));
                event.setErrorMessage(e.getMessage());
                log.error("Failed to process outbox event ID: {} (Attempt {}/3)", event.getId(), event.getRetryCount(), e);
            }
        }

        outboxEventRepository.saveAll(pendingEvents);
    }

    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void clearCompletedOutboxEvents() {
        outboxEventRepository.deleteCompletedOutboxEvents(LocalDateTime.now(clock).minusDays(1));
    }
}