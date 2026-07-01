package tsu.finalproject.feature.enrollment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tsu.finalproject.feature.enrollment.enums.EnrollmentStatus;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    Optional<Enrollment> findByStudentIdAndCourseId(Long studentId, Long courseId);

    @EntityGraph(attributePaths = {"course", "selectedSessions"})
    List<Enrollment> findByStudentIdAndStatus(Long studentId, EnrollmentStatus status);

    Page<Enrollment> findByCourseId(Long courseId, Pageable pageable);

    @Query("SELECT e.student.id FROM Enrollment e WHERE e.course.id = :courseId AND e.status = 'ENROLLED'")
    List<Long> findEnrolledStudentIdsByCourseId(@Param("courseId") Long courseId);

    @EntityGraph(attributePaths = {"course", "course.semester"})
    List<Enrollment> findByStudentId(Long studentId);
}
