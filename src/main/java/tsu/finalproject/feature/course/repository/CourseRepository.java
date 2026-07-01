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

    @EntityGraph(attributePaths = {"semester", "leadProfessor"})
    @Query("""
                SELECT c FROM Course c
                WHERE c.deactivated = false
                AND (:search IS NULL OR
                     LOWER(c.title) LIKE LOWER(CONCAT('%', :search, '%')) OR
                     LOWER(c.faculty) LIKE LOWER(CONCAT('%', :search, '%')) OR
                     LOWER(c.leadProfessor.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR
                     LOWER(c.leadProfessor.lastName) LIKE LOWER(CONCAT('%', :search, '%')))
                AND (:semesterId IS NULL OR c.semester.id = :semesterId)
            """)
    Page<Course> searchActiveCourses(@Param("search") String search, @Param("semesterId") Long semesterId, Pageable pageable);

    @EntityGraph(attributePaths = {"semester", "leadProfessor"})
    @Query("""
                SELECT c FROM Course c
                WHERE c.deactivated = false
                AND (c.leadProfessor.id = :professorId OR EXISTS (
                    SELECT 1 FROM CourseSession ses
                    WHERE ses.section.course.id = c.id AND ses.professor.id = :professorId
                ))
            """)
    Page<Course> findCoursesByProfessorInvolvement(@Param("professorId") Long professorId, Pageable pageable);
}
