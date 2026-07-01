package tsu.finalproject.feature.submission.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tsu.finalproject.feature.submission.entity.Submission;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {

    Optional<Submission> findByStudentIdAndPostId(Long studentId, Long postId);

    Page<Submission> findByPostId(Long postId, Pageable pageable);

    Page<Submission> findByPostIdAndScoreIsNull(Long postId, Pageable pageable);

    Page<Submission> findByStudentIdOrderBySubmittedAtDesc(Long studentId, Pageable pageable);

    @Query("SELECT s FROM Submission s WHERE s.student.id = :studentId AND s.post.course.id = :courseId AND s.score IS NOT NULL")
    List<Submission> findGradedSubmissionsByStudentAndCourse(@Param("studentId") Long studentId, @Param("courseId") Long courseId);
}