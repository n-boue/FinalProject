package tsu.finalproject.feature.submission.entity;

import jakarta.persistence.*;
import lombok.*;
import tsu.finalproject.feature.feed.entity.QuizQuestion;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "quiz_answers",
        uniqueConstraints = {
                // A student can only have one logged answer per question per submission
                @UniqueConstraint(
                        name = "uk_submission_question",
                        columnNames = {"submission_id", "question_id"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submission_id", nullable = false)
    private Submission submission;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private QuizQuestion question;

    // Used if QuestionType is SHORT_ANSWER
    @Column(name = "provided_text", columnDefinition = "TEXT")
    private String providedText;

    // Used if QuestionType is MULTIPLE_CHOICE or TRUE_FALSE (holds the index they picked)
    @ElementCollection
    @CollectionTable(name = "quiz_answer_selections", joinColumns = @JoinColumn(name = "answer_id"))
    @Column(name = "selected_index", nullable = false)
    @Builder.Default
    private List<Integer> selectedOptionIndices = new ArrayList<>();

    @Column(name = "awarded_points")
    private Integer awardedPoints;
}