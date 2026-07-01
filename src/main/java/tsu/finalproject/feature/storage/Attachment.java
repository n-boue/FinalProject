package tsu.finalproject.feature.storage;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.NaturalId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import tsu.finalproject.feature.user.entity.User;

import java.time.LocalDateTime;


@Entity
@Table(name = "attachments")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Attachment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank
    private String originalFileName;

    @NaturalId
    @Column(nullable = false, unique = true, name = "object_key")
    @NotBlank
    private String objectKey;

    @Column(nullable = false, name = "content_type")
    @NotBlank
    private String contentType;

    @Column(nullable = false, name = "size_in_bytes")
    @NotNull
    private Long sizeBytes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by_id", nullable = false)
    private User uploadedBy;

    @Column(name = "created_at", updatable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Attachment that)) return false;
        return getObjectKey() != null && getObjectKey().equals(that.getObjectKey());
    }

    @Override
    public int hashCode() {
        return objectKey != null ? objectKey.hashCode() : getClass().hashCode();
    }
}
