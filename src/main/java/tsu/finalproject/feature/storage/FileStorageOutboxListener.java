package tsu.finalproject.feature.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import tsu.finalproject.common.OutboxEvent;
import tsu.finalproject.common.OutboxEventRepository;
import tsu.finalproject.common.OutboxStatus;
import tsu.finalproject.feature.storage.event.FileDeletionEvent;

@Component
@RequiredArgsConstructor
public class FileStorageOutboxListener {

    private final OutboxEventRepository outboxEventRepository;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handleFileDeletion(FileDeletionEvent event) {
        OutboxEvent outboxEvent = OutboxEvent.builder()
                                          .eventType("FILE_DELETION")
                                          .payload(event.objectKey())
                                          .status(OutboxStatus.PENDING)
                                          .build();

        outboxEventRepository.save(outboxEvent);
    }
}