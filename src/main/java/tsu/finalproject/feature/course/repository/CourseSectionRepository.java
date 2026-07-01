package tsu.finalproject.feature.course.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tsu.finalproject.feature.course.entity.CourseSection;

@Repository
public interface CourseSectionRepository extends JpaRepository<CourseSection, Long> {

}