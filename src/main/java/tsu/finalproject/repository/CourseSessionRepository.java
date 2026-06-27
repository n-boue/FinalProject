package tsu.finalproject.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tsu.finalproject.entity.course.CourseSession;

import java.util.List;

@Repository
public interface CourseSessionRepository extends JpaRepository<CourseSession, Long> {
    @EntityGraph(attributePaths = {"section", "section.course"})
    List<CourseSession> findByProfessorId(Long professorId);
}
