package tsu.finalproject.feature.feed.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@DiscriminatorValue("QUIZ")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class Quiz extends Post {

    @Column(name = "due_date")
    private LocalDateTime dueDate;

    @Column(name = "time_limit_minutes")
    private Integer timeLimitMinutes;

    @Column(name = "passing_score")
    private Integer passingScore;

    @Column(name = "shuffle_questions")
    private Boolean shuffleQuestions;

    @Builder.Default
    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<QuizQuestion> questions = new HashSet<>();
}