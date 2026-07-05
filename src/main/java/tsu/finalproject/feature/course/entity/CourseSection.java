package tsu.finalproject.feature.course.entity;

import jakarta.persistence.*;
import lombok.*;
import tsu.finalproject.feature.course.enums.SessionType;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "course_sections")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseSection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SessionType type;

    // The available timeslots/groups for this specific section
    @OneToMany(mappedBy = "section", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<CourseSession> sessions = new HashSet<>();
}
