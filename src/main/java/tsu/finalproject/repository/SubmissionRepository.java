package tsu.finalproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tsu.finalproject.entity.feed.Submission;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    Optional<Submission> findByStudentIdAndPostId(Long studentId, Long postId);

    List<Submission> findByPostId(Long postId);
}
