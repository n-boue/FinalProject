package tsu.finalproject.feature.course.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import tsu.finalproject.feature.semester.Semester;
import tsu.finalproject.feature.user.entity.Professor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "courses")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank
    private String title;

    @Column(nullable = false)
    @NotBlank
    private String faculty;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    @NotNull
    @Min(0)
    private Integer credits;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "semester_id", nullable = false)
    @NotNull
    private Semester semester;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<CourseSection> sections = new HashSet<>();

    @Column(name = "created_at", updatable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private boolean deactivated;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lead_professor_id", nullable = false)
    private Professor leadProfessor;
}
