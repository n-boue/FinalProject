package tsu.finalproject.feature.enrollment;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import tsu.finalproject.feature.submission.entity.Submission;
import tsu.finalproject.feature.submission.event.SubmissionGradedEvent;
import tsu.finalproject.feature.submission.repository.SubmissionRepository;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GradeAggregationListener {

    private final EnrollmentRepository enrollmentRepository;
    private final SubmissionRepository submissionRepository;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleSubmissionGraded(SubmissionGradedEvent event) {
        enrollmentRepository.findByStudentIdAndCourseId(event.studentId(), event.courseId())
                .ifPresent(enrollment -> {
                    List<Submission> gradedSubmissions = submissionRepository
                                                                 .findGradedSubmissionsByStudentAndCourse(event.studentId(), event.courseId());

                    BigDecimal totalScore = gradedSubmissions.stream()
                                                    .map(Submission::getScore)
                                                    .reduce(BigDecimal.ZERO, BigDecimal::add);

                    enrollment.setFinalScore(totalScore);

                    if (totalScore.compareTo(BigDecimal.valueOf(90)) >= 0) {
                        enrollment.setFinalGrade("A");
                    } else if (totalScore.compareTo(BigDecimal.valueOf(80)) >= 0) {
                        enrollment.setFinalGrade("B");
                    } else if (totalScore.compareTo(BigDecimal.valueOf(70)) >= 0) {
                        enrollment.setFinalGrade("C");
                    } else if (totalScore.compareTo(BigDecimal.valueOf(60)) >= 0) {
                        enrollment.setFinalGrade("D");
                    } else {
                        enrollment.setFinalGrade("F");
                    }

                    enrollmentRepository.save(enrollment);
                });
    }
}