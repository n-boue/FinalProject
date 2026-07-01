package tsu.finalproject.feature.course.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.NaturalId;
import tsu.finalproject.feature.user.entity.Professor;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "course_sessions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_section_id", nullable = false)
    private CourseSection section;

    @Column
    private String name;

    @Column(name = "max_capacity", nullable = false)
    @NotNull
    @Min(0)
    private Integer maxCapacity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "professor_id", nullable = false)
    private Professor professor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DayOfWeek dayOfWeek;

    @Column(name = "start_time", nullable = false)
    @NotNull
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    @NotNull
    private LocalTime endTime;

    @Column(nullable = false)
    @NotBlank
    private String room;

    @NaturalId
    @Column(nullable = false, unique = true, updatable = false)
    @Builder.Default
    private String businessKey = UUID.randomUUID().toString();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CourseSession that)) return false;
        return getBusinessKey() != null && getBusinessKey().equals(that.getBusinessKey());
    }

    @Override
    public int hashCode() {
        return businessKey != null ? businessKey.hashCode() : getClass().hashCode();
    }
}
