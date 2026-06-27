package tsu.finalproject.entity.feed;

import jakarta.persistence.*;
import lombok.*;
import tsu.finalproject.entity.user.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String message;

    @Column(nullable = false)
    private String targetUrl;

    @Column(nullable = false)
    private boolean isRead;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}