package tsu.finalproject.feature.submission.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tsu.finalproject.feature.submission.entity.Submission;

import java.util.Optional;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {

    Optional<Submission> findByStudentIdAndPostId(Long studentId, Long postId);

    Page<Submission> findByPostId(Long postId, Pageable pageable);

    Page<Submission> findByPostIdAndScoreIsNull(Long postId, Pageable pageable);
}