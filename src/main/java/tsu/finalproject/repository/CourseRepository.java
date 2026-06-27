package tsu.finalproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tsu.finalproject.entity.course.Course;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findBySemesterIdAndDeactivatedFalse(Long semesterId);
    List<Course> findByLeadProfessorIdAndSemesterId(Long professorId, Long semesterId);
}
