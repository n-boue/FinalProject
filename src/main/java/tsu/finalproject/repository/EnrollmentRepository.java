package tsu.finalproject.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tsu.finalproject.entity.course.Enrollment;
import tsu.finalproject.entity.enums.EnrollmentStatus;

import java.util.Optional;
import java.util.List;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    Optional<Enrollment> findByStudentIdAndCourseId(Long studentId, Long courseId);

    @EntityGraph(attributePaths = {"course", "selectedSessions"})
    List<Enrollment> findByStudentIdAndStatus(Long studentId, EnrollmentStatus status);
}
