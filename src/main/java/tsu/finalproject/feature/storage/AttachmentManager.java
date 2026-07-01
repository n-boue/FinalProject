package tsu.finalproject.feature.storage;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import tsu.finalproject.feature.feed.entity.Post;
import tsu.finalproject.feature.storage.event.FileDeletionEvent;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AttachmentManager {

    private final AttachmentRepository attachmentRepository;
    private final ApplicationEventPublisher eventPublisher;

    public List<Attachment> fetchAttachments(List<String> objectKeys) {
        if (objectKeys == null || objectKeys.isEmpty()) return List.of();
        return objectKeys.stream()
                       .map(key -> attachmentRepository.findByObjectKey(key)
                                           .orElseThrow(() -> new EntityNotFoundException("Attachment not found for key: " + key)))
                       .collect(Collectors.toList());
    }

    public void syncAttachments(Post post, List<String> requestedKeys) {
        if (requestedKeys == null) requestedKeys = List.of();

        List<String> finalRequestedKeys = requestedKeys;
        List<Attachment> currentAttachments = post.getAttachments();

        List<Attachment> attachmentsToRemove = currentAttachments.stream()
                                                       .filter(att -> !finalRequestedKeys.contains(att.getObjectKey()))
                                                       .toList();

        attachmentsToRemove.forEach(att -> eventPublisher.publishEvent(new FileDeletionEvent(att.getObjectKey())));
        currentAttachments.removeAll(attachmentsToRemove);

        List<String> currentKeys = currentAttachments.stream().map(Attachment::getObjectKey).toList();
        List<String> newKeysToAdd = requestedKeys.stream().filter(key -> !currentKeys.contains(key)).toList();

        currentAttachments.addAll(fetchAttachments(newKeysToAdd));
    }
}