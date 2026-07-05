package tsu.finalproject.feature.storage;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import tsu.finalproject.feature.feed.entity.Post;
import tsu.finalproject.feature.storage.event.FileDeletionEvent;
import tsu.finalproject.feature.user.entity.User;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AttachmentManager {

    private final AttachmentRepository attachmentRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final FileStorageService fileStorageService;

    public Set<Attachment> fetchAttachments(List<String> objectKeys) {
        if (objectKeys == null || objectKeys.isEmpty()) return Set.of();
        return objectKeys.stream()
                       .map(key -> attachmentRepository.findByObjectKey(key)
                                           .orElseThrow(() -> new EntityNotFoundException("Attachment not found for key: " + key)))
                       .collect(Collectors.toSet());
    }

    @Transactional
    @NonNull
    public Attachment uploadAndRegisterAttachment(@NonNull MultipartFile file, @NonNull String folderPrefix, @NonNull User uploader) {
        String objectKey = fileStorageService.uploadFile(file, folderPrefix);

        Attachment attachment = Attachment.builder()
                                        .originalFileName(file.getOriginalFilename())
                                        .objectKey(objectKey)
                                        .contentType(file.getContentType())
                                        .sizeBytes(file.getSize())
                                        .uploadedBy(uploader)
                                        .build();

        return attachmentRepository.save(attachment);
    }

    public String getAttachmentUrl(@NonNull Attachment attachment) {
        return fileStorageService.getFileUrl(attachment.getObjectKey());
    }

    public void publishDeletionEvent(@NonNull Attachment attachment) {
        eventPublisher.publishEvent(new FileDeletionEvent(attachment.getObjectKey()));
    }

    public void syncAttachments(Post post, List<String> requestedKeys) {
        if (requestedKeys == null) requestedKeys = List.of();

        List<String> finalRequestedKeys = requestedKeys;
        Set<Attachment> currentAttachments = post.getAttachments();

        Set<Attachment> attachmentsToRemove = currentAttachments.stream()
                                                       .filter(att -> !finalRequestedKeys.contains(att.getObjectKey()))
                                                       .collect(Collectors.toSet());

        attachmentsToRemove.forEach(this::publishDeletionEvent);
        currentAttachments.removeAll(attachmentsToRemove);

        List<String> currentKeys = currentAttachments.stream().map(Attachment::getObjectKey).toList();
        List<String> newKeysToAdd = requestedKeys.stream().filter(key -> !currentKeys.contains(key)).toList();

        currentAttachments.addAll(fetchAttachments(newKeysToAdd));
    }
}