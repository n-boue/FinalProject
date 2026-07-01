package tsu.finalproject.feature.course.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tsu.finalproject.feature.course.entity.Course;

import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    Page<Course> findBySemesterIdAndDeactivatedFalse(Long semesterId, Pageable pageable);

    Page<Course> findByLeadProfessorIdAndSemesterId(Long professorId, Long semesterId, Pageable pageable);

    @EntityGraph(attributePaths = {"sections", "sections.sessions", "sections.sessions.professor", "semester", "leadProfessor"})
    @Query("SELECT c FROM Course c WHERE c.id = :id")
    Optional<Course> findCourseDetailsById(@Param("id") Long id);
}
